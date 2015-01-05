BRANCH=master
LEIN = lein
TEST_ARGS = midje
TMP_DIR := $(shell gmktemp -u 2>/dev/null || mktemp -u)
CLJ_FILES := $(shell find . -name "*.clj")
export LEIN_SNAPSHOTS_IN_RELEASE=1


all: uberjar

bump-major:
	${LEIN} ver bump :major

bump-minor:
	${LEIN} ver bump :minor

bump-patch:
	${LEIN} ver bump :patch

deps:
	@${LEIN} deps

minor-release: bump-minor tag-release

major-release: bump-major tag-release

patch-release: bump-patch tag-release

package:
	@-rm *.tar.gz
	@mkdir -p ${TMP_DIR}/gat && \
		cp -r resources $(shell find target -name "gat.jar") \
			${TMP_DIR}/gat && \
	cd ${TMP_DIR}/gat/.. && \
	tar cvzf gat.tar.gz gat
	@cp ${TMP_DIR}/gat.tar.gz .
	@rm -r ${TMP_DIR}

tag-release:
	git tag -a $(shell ${LEIN} ver) -m "New Release" && \
	git add project.clj resources/VERSION && \
	git commit -m ":sparkles: Bump to $(shell ${LEIN} ver) :sparkles:" && \
	git push origin $(BRANCH) && \
	git push --tags || \
	(git checkout project.clj resources/VERSION && \
	exit 1)

target/uberjar/gat.jar: ${CLJ_FILES}
	@${LEIN} uberjar

test: deps
	@${LEIN} ${TEST_ARGS}

uberjar: target/uberjar/gat.jar

.PHONY: \
	all \
	bump-major \
	bump-minor \
	bump-patch \
	bump \
	deps \
	major-release \
	minor-release \
	package \
	patch-release \
	tag-release \
	test \
	uberjar \
	ver
