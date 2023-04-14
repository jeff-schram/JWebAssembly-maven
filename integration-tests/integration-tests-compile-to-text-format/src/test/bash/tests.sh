#!/usr/bin/env bash
# shellcheck disable=SC2068,SC2086,SC2207

ERRORS=0
WAT_FILE="$(ls target/*.wat)"

function shouldHaveCreatedWatFile() {
  if [[ -z "${WAT_FILE}" ]]; then echo "FAILED: ${FUNCNAME[0]}" && return 1; fi
}

function shouldHaveCreateExpectedWebAssemblyText() {
  diff ${WAT_FILE} src/test/resources/expected_result.wat
  if [[ $? -ne 0 ]]; then echo "FAILED: ${FUNCNAME[0]}" && return 1; fi
}

shouldHaveCreatedWatFile || ((ERRORS+=1))
shouldHaveCreateExpectedWebAssemblyText || ((ERRORS+=1))

exit ${ERRORS}
