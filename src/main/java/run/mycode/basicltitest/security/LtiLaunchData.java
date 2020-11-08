package run.mycode.basicltitest.security;

import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author dahlem.brian
 */
public class LtiLaunchData {
    private static final Logger LOG = LogManager.getLogger(LtiLaunchData.class);
    
    public static final String NAME = "LtiLaunchData";
    
    public static enum Parameter {
        CONTEXT_ID("context_id"),
        CONTEXT_LABEL("context_label"),
        CONTEXT_TITLE("context_title"),
        CONTEXT_TYPE("context_type"),
        LAUNCH_PRESENTATION_RETURN_URL("launch_presentation_return_url"),
        LAUNCH_PRESENTATION_LOCALE("launch_presentation_locale"),
        LAUNCH_PRESENTATION_CSS_URL("launch_presentation_css_url"),
        LAUNCH_PRESENTATION_DOCUMENT_TYPE("launch_presentation_document_target"),
        LAUNCH_PRESENTATION_HEIGHT("launch_presentation_height"),
        LAUNCH_PRESENTATION_WIDTH("launch_presentation_width"),
        LIS_PERSON_NAME_GIVEN("lis_person_name_given"),
        LIS_PERSON_NAME_FAMILY("lis_person_name_family"),
        LIS_PERSON_NAME_FULL("lis_person_name_full"),
        LIS_CONTACT_EMAIL_PRIMARY("lis_contact_email_primary"),
        LIS_RESULT_SOURCEDID("lis_result_sourcedid"),
        LIS_OUTCOME_SERVICE_URL("lis_outcome_service_url"),
        LTI_MESSAGE_TYPE("lti_message_type"),
        LTI_VERSION("lti_version"),
        RESOURCE_LINK_DESCRIPTION("resource_link_description"),
        RESOURCE_LINK_ID("resource_link_id"),
        RESOURCE_LINK_TITLE("resource_link_title"),
        ROLE_SCOPE_MENTOR("role_scope_mentor"),
        ROLES("roles"),
        TOOL_CONSUMER_INFO_PRODUCT_FAMILY_CODE("tool_consumer_info_product_family_code"),
        TOOL_CONSUMER_INFO_VERSION("tool_consumer_info_version"),
        TOOL_CONSUMER_INSTANCE_CONTACT_EMAIL("tool_consumer_instance_contact_email"),
        TOOL_CONSUMER_INSTANCE_DESCRIPTION("tool_consumer_instance_description"),
        TOOL_CONSUMER_INSTANCE_GUID("tool_consumer_instance_guid"),
        TOOL_CONSUMER_INSTANCE_NAME("tool_consumer_instance_name"),
        TOOL_CONSUMER_INSTANCE_URL("tool_consumer_instance_url"),
        USER_ID("user_id"),
        USER_IMAGE("user_image");
    
        public final String key;
        private Parameter(String key) {
            this.key = key;
        }
    };
    
    private final Map<String, String> data;
    
    public LtiLaunchData(HttpServletRequest request) {
        data = new HashMap<>();
        
        for (Parameter param : Parameter.values()) {
            String val = request.getParameter(param.key);
            
            if (val != null) {
                data.put(param.key, val);
                LOG.debug(() -> (param + ": " + val));
            }
        }
    }
    
    public String get(String param) {
        return data.get(param);
    }
    
    public String get(Parameter param) {
        return data.get(param.key);
    }
}
