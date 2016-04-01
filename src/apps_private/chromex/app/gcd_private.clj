(ns chromex.app.gcd-private
  "Use the chrome.gcdPrivate API to discover GCD APIs and register
   them.

     * available since Chrome 40"

  (:refer-clojure :only [defmacro defn apply declare meta let partial])
  (:require [chromex.wrapgen :refer [gen-wrap-helper]]
            [chromex.callgen :refer [gen-call-helper gen-tap-all-events-call]]))

(declare api-table)
(declare gen-call)

; -- functions --------------------------------------------------------------------------------------------------------------

(defmacro get-device-info
  "Returns local device information.

     |service-name| - The mDns service name of the device.

   This function returns a core.async channel which eventually receives a result value and closes.
   Signature of the result value put on the channel is [status device-info] where:

     |status| - The status of operation (success or type of error).
     |device-info| - Content of /privet/info response. https://developers.google.com/cloud-devices/v1/reference/local-api/info

   In case of error the channel closes without receiving any result and relevant error object can be obtained via
   chromex.error/get-last-error."
  ([service-name] (gen-call :function ::get-device-info &form service-name)))

(defmacro create-session
  "Create new pairing.

     |service-name| - The mDns service name of the device.

   This function returns a core.async channel which eventually receives a result value and closes.
   Signature of the result value put on the channel is [session-id status pairing-types] where:

     |session-id| - The session ID (identifies the session for future calls).
     |status| - The status of operation (success or type of error). |pairingTypes| is the list of supported pairing types.
     |pairing-types| - ?

   In case of error the channel closes without receiving any result and relevant error object can be obtained via
   chromex.error/get-last-error."
  ([service-name] (gen-call :function ::create-session &form service-name)))

(defmacro start-pairing
  "Start pairing with selected method. Should be called after |establishSession|.

     |session-id| - The ID of the session created with |establishSession|.
     |pairing-type| - The value selected from the list provided in callback of |establishSession|.

   This function returns a core.async channel which eventually receives a result value and closes.
   Signature of the result value put on the channel is [status] where:

     |status| - ?

   In case of error the channel closes without receiving any result and relevant error object can be obtained via
   chromex.error/get-last-error."
  ([session-id pairing-type] (gen-call :function ::start-pairing &form session-id pairing-type)))

(defmacro confirm-code
  "Confirm pairing code. Should be called after |startPairing|.

     |session-id| - The ID of the session created with |establishSession|.
     |code| - The string generated by pairing process and available to the user.

   This function returns a core.async channel which eventually receives a result value and closes.
   Signature of the result value put on the channel is [status] where:

     |status| - ?

   In case of error the channel closes without receiving any result and relevant error object can be obtained via
   chromex.error/get-last-error."
  ([session-id code] (gen-call :function ::confirm-code &form session-id code)))

(defmacro send-message
  "Send an encrypted message to the device. If the message is a setup message with a wifi SSID specified but no password, the
   password cached by |prefetchWifiPassword| will be used and the call will fail if it's not available. For open networks use
   an empty string as the password.

     |session-id| - The ID of the session created with |establishSession|.
     |api| - The Privet API name to call.
     |input| - Input data for |api|.

   This function returns a core.async channel which eventually receives a result value and closes.
   Signature of the result value put on the channel is [status response] where:

     |status| - The status of operation (success or type of error).
     |response| - The response object with result or error description. May be empty for some errors.

   In case of error the channel closes without receiving any result and relevant error object can be obtained via
   chromex.error/get-last-error."
  ([session-id api input] (gen-call :function ::send-message &form session-id api input)))

(defmacro terminate-session
  "Terminate the session with the device.

     |session-id| - The ID of the session created with |establishSession|."
  ([session-id] (gen-call :function ::terminate-session &form session-id)))

; -- convenience ------------------------------------------------------------------------------------------------------------

(defmacro tap-all-events
  "Taps all valid non-deprecated events in chromex.app.gcd-private namespace."
  [chan]
  (gen-tap-all-events-call api-table (meta &form) chan))

; ---------------------------------------------------------------------------------------------------------------------------
; -- API TABLE --------------------------------------------------------------------------------------------------------------
; ---------------------------------------------------------------------------------------------------------------------------

(def api-table
  {:namespace "chrome.gcdPrivate",
   :since "40",
   :functions
   [{:id ::get-device-info,
     :name "getDeviceInfo",
     :since "44",
     :callback? true,
     :params
     [{:name "service-name", :type "string"}
      {:name "callback",
       :type :callback,
       :callback {:params [{:name "status", :type "gcdPrivate.Status"} {:name "device-info", :type "object"}]}}]}
    {:id ::create-session,
     :name "createSession",
     :since "43",
     :callback? true,
     :params
     [{:name "service-name", :type "string"}
      {:name "callback",
       :type :callback,
       :callback
       {:params
        [{:name "session-id", :type "integer"}
         {:name "status", :type "gcdPrivate.Status"}
         {:name "pairing-types", :type "[array-of-gcdPrivate.PairingTypes]"}]}}]}
    {:id ::start-pairing,
     :name "startPairing",
     :callback? true,
     :params
     [{:name "session-id", :type "integer"}
      {:name "pairing-type", :type "gcdPrivate.PairingType"}
      {:name "callback", :type :callback, :callback {:params [{:name "status", :type "gcdPrivate.Status"}]}}]}
    {:id ::confirm-code,
     :name "confirmCode",
     :callback? true,
     :params
     [{:name "session-id", :type "integer"}
      {:name "code", :type "string"}
      {:name "callback", :type :callback, :callback {:params [{:name "status", :type "gcdPrivate.Status"}]}}]}
    {:id ::send-message,
     :name "sendMessage",
     :callback? true,
     :params
     [{:name "session-id", :type "integer"}
      {:name "api", :type "string"}
      {:name "input", :type "object"}
      {:name "callback",
       :type :callback,
       :callback {:params [{:name "status", :type "gcdPrivate.Status"} {:name "response", :type "object"}]}}]}
    {:id ::terminate-session, :name "terminateSession", :params [{:name "session-id", :type "integer"}]}]})

; -- helpers ----------------------------------------------------------------------------------------------------------------

; code generation for native API wrapper
(defmacro gen-wrap [kind item-id config & args]
  (apply gen-wrap-helper api-table kind item-id config args))

; code generation for API call-site
(def gen-call (partial gen-call-helper api-table))