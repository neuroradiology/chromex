(ns chromex.app.resources-private
  "resourcesPrivate.
   
     * available since Chrome 47
     * https://developer.chrome.com/extensions/resourcesPrivate"

  (:refer-clojure :only [defmacro defn apply declare meta let])
  (:require [chromex.wrapgen :refer [gen-wrap-from-table]]
            [chromex.callgen :refer [gen-call-from-table gen-tap-all-call]]
            [chromex.config :refer [get-static-config gen-active-config]]))

(declare api-table)
(declare gen-call)

; -- functions --------------------------------------------------------------------------------------------------------------

(defmacro get-strings
  "Gets localized strings for a component extension. Includes default WebUI loadTimeData values for text and language settings
   (fontsize, fontfamily, language, textdirection). See
   chrome/browser/extensions/api/resources_private/resources_private_api.cc for instructions on adding a new component to this
   API.
   
     |component| - Internal chrome component to get strings for.
     |callback| - Called with a dictionary mapping names to strings.
   
   Note: Instead of passing a callback function, you receive a core.async channel as return value."
  ([component #_callback] (gen-call :function ::get-strings &form component)))

; -- convenience ------------------------------------------------------------------------------------------------------------

(defmacro tap-all-events
  "Taps all valid non-deprecated events in this namespace."
  [chan]
  (let [static-config (get-static-config)
        config (gen-active-config static-config)]
    (gen-tap-all-call static-config api-table (meta &form) config chan)))

; ---------------------------------------------------------------------------------------------------------------------------
; -- API TABLE --------------------------------------------------------------------------------------------------------------
; ---------------------------------------------------------------------------------------------------------------------------

(def api-table
  {:namespace "chrome.resourcesPrivate",
   :since "47",
   :functions
   [{:id ::get-strings,
     :name "getStrings",
     :callback? true,
     :params
     [{:name "component", :type "unknown-type"}
      {:name "callback", :type :callback, :callback {:params [{:name "result", :type "object"}]}}]}]})

; -- helpers ----------------------------------------------------------------------------------------------------------------

; code generation for native API wrapper
(defmacro gen-wrap [kind item-id config & args]
  (let [static-config (get-static-config)]
    (apply gen-wrap-from-table static-config api-table kind item-id config args)))

; code generation for API call-site
(defn gen-call [kind item src-info & args]
  (let [static-config (get-static-config)
        config (gen-active-config static-config)]
    (apply gen-call-from-table static-config api-table kind item src-info config args)))