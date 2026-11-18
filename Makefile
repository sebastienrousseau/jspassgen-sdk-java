# SPDX-License-Identifier: Apache-2.0 OR MIT
#
# `make check` runs every local gate CI's build job runs: the version
# check, whitespace format, file size, the Maven build (javac -Xlint:all
# -Werror, unit tests, JaCoCo at 100 % line and branch, PMD complexity
# ceilings) and the Javadoc with doclint. DEVELOPMENT.md maps every CI
# job to its target.
#
# ./mvnw pins Maven 3.10.1 (SHA-256 checked) and .mvn/maven.config makes
# Maven refuse any artifact whose SHA-256 is not recorded in
# .mvn/checksums/. After a dependency or plugin change, `make lock`
# re-records them from a clean local repository; review the diff.

MVN ?= ./mvnw
OSV_SCANNER ?= osv-scanner
LOCK_REPO ?= /tmp/jspassgen-sdk-java-lock-repo
TC := -Daether.artifactResolver.postProcessor.trustedChecksums
JAVA_FILES := $(shell find src -name '*.java' | LC_ALL=C sort)

.PHONY: help check versions format-check size verify build test lint javadoc docs-lint lock sbom audit clean

help: ## List the targets
	@grep -E '^[a-z-]+:.*## ' $(MAKEFILE_LIST) | sed 's/:.*## /: /'

check: versions format-check size verify javadoc ## Every local gate

versions: ## Every file states the version in pom.xml
	@scripts/verify-release-versions

format-check: ## No tabs, no trailing spaces, LF endings, final newline; library within 100 columns
	@bad=0; \
	for f in $(filter-out src/test/%,$(JAVA_FILES)); do \
	  if awk 'length > 100 {print FILENAME ":" FNR ": over 100 columns"; found = 1} END {exit !found}' "$$f"; then bad=1; fi; \
	done; \
	for f in $(JAVA_FILES) pom.xml config/pmd/complexity.xml; do \
	  if grep -n "$$(printf '\t')" "$$f"; then echo "tab in $$f"; bad=1; fi; \
	  if grep -nE ' +$$' "$$f"; then echo "trailing space in $$f"; bad=1; fi; \
	  if grep -q "$$(printf '\r')" "$$f"; then echo "CRLF in $$f"; bad=1; fi; \
	  if [ -n "$$(tail -c1 "$$f")" ]; then echo "no final newline in $$f"; bad=1; fi; \
	done; exit $$bad

size: ## Every Java file is at most 500 lines
	@bad=0; \
	for f in $(JAVA_FILES); do \
	  n=$$(wc -l < "$$f"); \
	  if [ "$$n" -gt 500 ]; then echo "$$f: $$n lines (limit 500)"; bad=1; fi; \
	done; exit $$bad

verify: ## Build, unit tests, JaCoCo 100 % line and branch, PMD complexity
	$(MVN) -B verify

build: ## Compile only
	$(MVN) -B -DskipTests compile

test: ## Unit tests
	$(MVN) -B test

lint: ## javac warnings as errors and the PMD complexity ceilings
	$(MVN) -B -DskipTests compile pmd:check

javadoc: ## The Javadoc (doclint all, warnings fail) into target/reports/apidocs
	$(MVN) -B -q -DskipTests compile javadoc:javadoc

docs-lint: ## markdownlint, codespell, REUSE and lychee (offline) over the docs
	npx --yes markdownlint-cli2@0.23.3 "**/*.md"
	codespell
	reuse lint
	lychee --offline --no-progress --include-fragments --exclude-path target '**/*.md'

lock: ## Re-record .mvn/checksums from a clean local repository
	rm -rf .mvn/checksums $(LOCK_REPO)
	$(MVN) -B -q -Dmaven.repo.local=$(LOCK_REPO) $(TC).record=true \
	  clean install pmd:check cyclonedx:makeBom javadoc:javadoc -DoutputFormat=json -DincludeTestScope=true
	rm -rf $(LOCK_REPO)

sbom: ## CycloneDX SBOM of the resolved dependencies (target/bom.json)
	$(MVN) -B -q cyclonedx:makeBom -DoutputFormat=json -DincludeTestScope=true

audit: sbom ## OSV-Scanner over the SBOM; fails on any known advisory
	@$(OSV_SCANNER) scan source -L target/bom.json > target/osv.txt 2>&1; rc=$$?; \
	cat target/osv.txt; \
	grep -Eq 'found [1-9][0-9]* packages' target/osv.txt || { echo "audit: no packages scanned"; exit 1; }; \
	exit $$rc

clean: ## Delete the Maven target directory
	$(MVN) -B -q clean
