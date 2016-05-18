(ns mondriaan.test-runner
  (:require
   [doo.runner :refer-macros [doo-tests]]
   [mondriaan.core-test]))

(enable-console-print!)

(doo-tests 'mondriaan.core-test)
