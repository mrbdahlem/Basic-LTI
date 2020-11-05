package run.mycode.basicltitest.security;

import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author dahlem.brian
 */
public class LtiLaunchData {
    public static final String NAME = "LtiLaunchData";
    
    private final String[] parameters = {
        "context_id",
        "context_label",
        "context_title",
        "context_type",
        "launch_presentation_return_url",
        "launch_presentation_locale",
        "lis_person_name_given",
        "lis_person_name_family",
        "lis_person_name_full",
        "lis_contact_email_primary",
        "lis_result_sourcedid",
        "lis_outcome_service_url",
        "lti_message_type",
        "lti_version",
        "launch_presentation_css_url",
        "launch_presentation_document_target",
        "launch_presentation_height",
        "launch_presentation_return_url",
        "launch_presentation_width",
        "resource_link_description",
        "resource_link_id",
        "resource_link_title",
        "role_scope_mentor",
        "roles",
        "tool_consumer_info_product_family_code",
        "tool_consumer_info_version",
        "tool_consumer_instance_contact_email",
        "tool_consumer_instance_description",
        "tool_consumer_instance_guid",
        "tool_consumer_instance_name",
        "tool_consumer_instance_url",
        "user_id",
        "user_image"
    };
    
    private final Map<String, String> data;
    
    public LtiLaunchData(HttpServletRequest request) {
        data = new HashMap<>();
        
        for (String param : parameters) {
            String val = request.getParameter(param);
            
            if (val != null) {
                data.put(param, val);
                System.out.println(param + ": " + val);
            }
        }
    }
    
    public String getLaunchParameter(String param) {
        return data.get(param);
    }
}
