package org.smartregister.service.formsubmissionhandler;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.smartregister.domain.form.FormSubmission;
import org.smartregister.repository.FormDataRepository;
import org.smartregister.util.Log;

import java.util.HashMap;
import java.util.Map;

import static java.text.MessageFormat.format;
import static org.smartregister.AllConstants.FormNames.ANC_CLOSE;
import static org.smartregister.AllConstants.FormNames.ANC_INVESTIGATIONS;
import static org.smartregister.AllConstants.FormNames.ANC_REGISTRATION;
import static org.smartregister.AllConstants.FormNames.ANC_REGISTRATION_OA;
import static org.smartregister.AllConstants.FormNames.ANC_VISIT;
import static org.smartregister.AllConstants.FormNames.CHILD_CLOSE;
import static org.smartregister.AllConstants.FormNames.CHILD_ILLNESS;
import static org.smartregister.AllConstants.FormNames.CHILD_IMMUNIZATIONS;
import static org.smartregister.AllConstants.FormNames.CHILD_REGISTRATION_EC;
import static org.smartregister.AllConstants.FormNames.CHILD_REGISTRATION_OA;
import static org.smartregister.AllConstants.FormNames.DELIVERY_OUTCOME;
import static org.smartregister.AllConstants.FormNames.DELIVERY_PLAN;
import static org.smartregister.AllConstants.FormNames.EC_CLOSE;
import static org.smartregister.AllConstants.FormNames.EC_EDIT;
import static org.smartregister.AllConstants.FormNames.EC_REGISTRATION;
import static org.smartregister.AllConstants.FormNames.FP_CHANGE;
import static org.smartregister.AllConstants.FormNames.FP_COMPLICATIONS;
import static org.smartregister.AllConstants.FormNames.HB_TEST;
import static org.smartregister.AllConstants.FormNames.IFA;
import static org.smartregister.AllConstants.FormNames.PNC_CLOSE;
import static org.smartregister.AllConstants.FormNames.PNC_REGISTRATION_OA;
import static org.smartregister.AllConstants.FormNames.PNC_VISIT;
import static org.smartregister.AllConstants.FormNames.RENEW_FP_PRODUCT;
import static org.smartregister.AllConstants.FormNames.TT;
import static org.smartregister.AllConstants.FormNames.TT_1;
import static org.smartregister.AllConstants.FormNames.TT_2;
import static org.smartregister.AllConstants.FormNames.TT_BOOSTER;
import static org.smartregister.AllConstants.FormNames.VITAMIN_A;
import static org.smartregister.event.Event.FORM_SUBMITTED;
import static org.smartregister.util.Log.logWarn;

public class FormSubmissionRouter {
    private final Map<String, FormSubmissionHandler> handlerMap;
    private FormDataRepository formDataRepository;

    public FormSubmissionRouter(FormDataRepository formDataRepository, ECEditHandler ecEditHandler, ANCInvestigationsHandler
                                        ancInvestigationsHandler) {
        this.formDataRepository = formDataRepository;
        handlerMap = new HashMap<String, FormSubmissionHandler>();
        handlerMap.put(EC_EDIT, ecEditHandler);
        handlerMap.put(ANC_INVESTIGATIONS, ancInvestigationsHandler);
    }

    public void route(String instanceId) throws Exception {
        FormSubmission submission = formDataRepository.fetchFromSubmission(instanceId);
        FormSubmissionHandler handler = handlerMap.get(submission.formName());
        if (handler == null) {
            logWarn("Could not find a handler due to unknown form submission: " + submission);
        } else {
            try {
                handler.handle(submission);
            } catch (Exception e) {
                Log.logError(format("Handling {0} form submission with instance Id: {1} for "
                                + "entity: {2} failed with exception : {3}", submission.formName(),
                        submission.instanceId(), submission.entityId(),
                        ExceptionUtils.getStackTrace(e)));
                throw e;
            }
        }
        FORM_SUBMITTED.notifyListeners(instanceId);
    }

    public Map<String, FormSubmissionHandler> getHandlerMap() {
        return handlerMap;
    }

    public void handleSubmission(FormSubmission submission, String formName) {
        if (getHandlerMap().isEmpty()) {
            return;
        }

        FormSubmissionHandler handler = getHandlerMap().get(formName);
        if (handler != null) {
            handler.handle(submission);
        }
    }
}
