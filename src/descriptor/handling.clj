(ns descriptor.handling
  (:require
    [tg-bot-api.telegram :as telegram]
    
    [clojure.spec.alpha :as spec]))

(def fixture
  {:update_id 779034871, 
   :channel_post {:message_id 585, 
                  :author_signature "макс", 
                  :sender_chat {:id -1001821581750, 
                                :title "stealer test ch", 
                                :type "channel"}, 
                  :chat        {:id -1001821581750, 
                                :title "stealer test ch", 
                                :type "channel"}, 
                  :date 1698500425, 
                  :audio {:duration 208, 
                          :file_name "фараон микс 0.2.mp3", 
                          :mime_type "audio/mpeg", 
                          :file_id "CQACAgIAAx0CbJMhtgACAkllPQ9IV6tzSDpfY8wb24Z7ZLbE4gACOTgAAs1l8Umc1NO8PoOs3DAE", 
                          :file_unique_id "AgADOTgAAs1l8Uk", 
                          :file_size 8326709}}})

(spec/def ::ne-string
  (spec/and string? not-empty))

(spec/def :audio/file_name ::ne-string)

(spec/def :channel_post/caption ::ne-string)

(spec/def :channel_post/audio 
  (spec/keys :req-un 
    [:audio/file_name]))

(spec/def :update/channel_post
  (spec/and
    (spec/keys :req-un
      [:channel_post/chat
       :channel_post/audio])
    (fn [x] (not (contains? x :caption)))))

(spec/def ::channel-post-with-audio
  (spec/keys :req-un
    [::update_id
     :update/channel_post]))

(comment
  (spec/explain ::channel-post-with-audio fixture))


(defn the-handler 
  "Bot logic here"
  [config {:keys [channel_post] :as update} trigger-id]
  
  (when (spec/valid? ::channel-post-with-audio update)
    
    (let [{:keys [chat audio message_id]}
          channel_post
          
          {:keys [id]}
          chat
          
          {:keys [file_name]}
          audio]
      
      (telegram/edit-message-caption config id message_id file_name))))
