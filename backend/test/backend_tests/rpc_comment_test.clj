;; This Source Code Form is subject to the terms of the Mozilla Public
;; License, v. 2.0. If a copy of the MPL was not distributed with this
;; file, You can obtain one at http://mozilla.org/MPL/2.0/.
;;
;; Copyright (c) KALEIDOS INC

(ns backend-tests.rpc-comment-test
  (:require
   [app.common.uuid :as uuid]
   [app.common.geom.point :as gpt]
   [app.db :as db]
   [app.http :as http]
   [app.rpc :as-alias rpc]
   [app.rpc.cond :as cond]
   [app.rpc.quotes :as-alias quotes]
   [backend-tests.helpers :as th]
   [clojure.test :as t]
   [datoteka.core :as fs]
   [mockery.core :refer [with-mocks]]))

(t/use-fixtures :once th/state-init)
(t/use-fixtures :each th/database-reset)

(t/deftest comment-and-threads-crud
  (with-mocks [mock {:target 'app.config/get
                     :return (th/config-get-mock
                              {:quotes-teams-per-profile 200})}]

    (let [profile-1 (th/create-profile* 1 {:is-active true})
          profile-2 (th/create-profile* 2 {:is-active true})

          team      (th/create-team* 1 {:profile-id (:id profile-1)})
          ;; role      (th/create-team-role* {:team-id (:id team)
          ;;                                  :profile-id (:id profile-2)
          ;;                                  :role :admin})

          project   (th/create-project* 1 {:team-id (:id team)
                                           :profile-id (:id profile-1)})
          file-1    (th/create-file* 1 {:profile-id (:id profile-1)
                                        :project-id (:id project)})
          file-2    (th/create-file* 2 {:profile-id (:id profile-1)
                                        :project-id (:id project)})
          page-id   (get-in file-1 [:data :pages 0])]
      ;; (app.common.pprint/pprint file-1))))
      (let [data {::th/type :create-comment-thread
                  ::rpc/profile-id (:id profile-1)
                  :file-id (:id file-1)
                  :page-id page-id
                  :position (gpt/point 0)
                  :content "hello world"
                  :frame-id uuid/zero}
            out  (th/command! data)]
        ;; (th/print-result! out)
        (t/is (th/success? out)))


      )))
