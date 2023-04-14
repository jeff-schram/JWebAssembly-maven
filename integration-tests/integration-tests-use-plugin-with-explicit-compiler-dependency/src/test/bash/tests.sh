#!/usr/bin/env bash
# shellcheck disable=SC2068,SC2086,SC2207

ERRORS=0
WASM_FILE="$(ls target/*.wasm)"

function shouldHaveCreatedWasmFile() {
  if [[ -z "${WASM_FILE}" ]]; then echo "FAILED: ${FUNCNAME[0]}" && return 1; fi
}

shouldHaveCreatedWasmFile || ((ERRORS+=1))

exit ${ERRORS}
