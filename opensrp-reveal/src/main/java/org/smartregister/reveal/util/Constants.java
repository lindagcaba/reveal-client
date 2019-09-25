package org.smartregister.reveal.util;

import java.util.Arrays;
import java.util.List;

public interface Constants {

    String VIEW_CONFIGURATION_PREFIX = "ViewConfiguration_";

    String FILTER_TEAM_ID = "teamId";

    String JSON_FORM_PARAM_JSON = "json";

    int REQUEST_CODE_GET_JSON = 3432;

    int REQUEST_CODE_GET_JSON_FRAGMENT = 3439;

    String METADATA = "metadata";

    String DETAILS = "details";

    String ENTITY_ID = "entity_id";

    String SPRAY_EVENT = "Spray";

    String REGISTER_STRUCTURE_EVENT = "Register_Structure";

    String MOSQUITO_COLLECTION_EVENT = "mosquito_collection";

    String LARVAL_DIPPING_EVENT = "larval_dipping";

    String BEDNET_DISTRIBUTION_EVENT = "bednet_distribution";

    String BLOOD_SCREENING_EVENT = "blood_screening";

    String BEHAVIOUR_CHANGE_COMMUNICATION = "behaviour_change_communication";

    String STRUCTURE = "Structure";

    String FOCUS = "FOCUS";

    double MY_LOCATION_ZOOM_LEVEL = 17.5; // modifying this will also necessitate modifying the VERTICAL_OFFSET

    double VERTICAL_OFFSET = 0.0;

    double REFRESH_MAP_MINIMUM_DISTANCE = 5;

    int ANIMATE_TO_LOCATION_DURATION = 1000;

    String DIGITAL_GLOBE_CONNECT_ID = "DG_CONNECT_ID";

    String HYPHEN = "-";

    String COMMA = ",";

    String NULL_KEY = "NULL";

    interface CONFIGURATION {
        String LOGIN = "login";
        String GLOBAL_CONFIGS = "global_configs";
        String TEAM_CONFIGS = "team_configs";
        String KEY = "key";
        String VALUE = "value";
        String SETTINGS = "settings";
        String LOCATION_BUFFER_RADIUS_IN_METRES = "location_buffer_radius_in_metres";
        String UPDATE_LOCATION_BUFFER_RADIUS = "update_location_buffer_radius";
        String INDEX_CASE_CIRCLE_RADIUS_IN_METRES = "index_case_circle_radius_in_metres";
        Float DEFAULT_INDEX_CASE_CIRCLE_RADIUS_IN_METRES = 1000f;
        String DRAW_OPERATIONAL_AREA_BOUNDARY_AND_LABEL = "draw_operational_area_boundary_and_label";
        Boolean DEFAULT_DRAW_OPERATIONAL_AREA_BOUNDARY_AND_LABEL = true;
        String LOCAL_SYNC_DONE = "local_sync_done";
        Float DEFAULT_GEO_JSON_CIRCLE_SIDES = 120F;
        Float METERS_PER_KILOMETER = 1000f;
        Float KILOMETERS_PER_DEGREE_OF_LONGITUDE_AT_EQUITOR = 111.320f;
        Float KILOMETERS_PER_DEGREE_OF_LATITUDE_AT_EQUITOR = 110.574f;
        String VALIDATE_FAR_STRUCTURES = "validate_far_structures";
        String RESOLVE_LOCATION_TIMEOUT_IN_SECONDS = "resolve_location_timeout_in_seconds";
        String ADMIN_PASSWORD_NOT_NEAR_STRUCTURES = "admin_password_not_near_structures";
        String DISPLAY_ADD_STRUCTURE_OUT_OF_BOUNDARY_WARNING_DIALOG = "display_add_structure_out_of_boundary_warning_dialog";
        Boolean DEFAULT_DISPLAY_ADD_STRUCTURE_OUT_OF_BOUNDARY_WARNING_DIALOG = false;
        Float OUTSIDE_OPERATIONAL_AREA_MASK_OPACITY = 0.65f;
    }

    interface Preferences {
        String CURRENT_FACILITY = "CURRENT_FACILITY";
        String CURRENT_DISTRICT = "CURRENT_DISTRICT";
        String CURRENT_PLAN = "CURRENT_PLAN";
        String CURRENT_PLAN_ID = "CURRENT_PLAN_ID";
        String FACILITY_LEVEL = "FACILITY_LEVEL";
        String CURRENT_OPERATIONAL_AREA = "CURRENT_OPERATIONAL_AREA";
    }

    interface Tags {
        String COUNTRY = "Country";
        String PROVINCE = "Province";
        String REGION = "Region";
        String DISTRICT = "District";
        String SUB_DISTRICT = "Sub-district";
        String HEALTH_CENTER = "Rural Health Centre";
        String CANTON = "Canton";
        String VILLAGE = "Village";
        String OPERATIONAL_AREA = "Operational Area";
    }

    interface Properties {
        String TASK_IDENTIFIER = "taskIdentifier";
        String TASK_BUSINESS_STATUS = "taskBusinessStatus";
        String TASK_STATUS = "taskStatus";
        String TASK_CODE = "taskCode";
        String LOCATION_UUID = "locationUUID";
        String LOCATION_VERSION = "locationVersion";
        String LOCATION_TYPE = "locationType";
        String LOCATION_PARENT = "locationParent";
        String LOCATION_ID = "location_id";
        String FEATURE_SELECT_TASK_BUSINESS_STATUS = "featureSelectTaskBusinessStatus";
        String BASE_ENTITY_ID = "baseEntityId";
        String STRUCTURE_NAME = "structure_name";
        String APP_VERSION_NAME = "appVersionName";
    }


    interface GeoJSON {
        String TYPE = "type";
        String FEATURE_COLLECTION = "FeatureCollection";
        String FEATURES = "features";
        String IS_INDEX_CASE = "is_index_case";
    }

    interface Intervention {
        String IRS = "IRS";

        String MOSQUITO_COLLECTION = "Mosquito Collection";

        String LARVAL_DIPPING = "Larval Dipping";

        String BCC = "BCC";

        String BEDNET_DISTRIBUTION = "Bednet Distribution";

        String BLOOD_SCREENING = "Blood Screening";

        String CASE_CONFIRMATION = "Case Confirmation";

        String REGISTER_FAMILY = "RACD Register Family";

        String FI = "FI";

        String PAOT = "PAOT";
    }


    interface EventType {

        String CASE_CONFIRMATION_EVENT = "case_confirmation";

        String CASE_DETAILS_EVENT = "Case Details";

        String PAOT_EVENT = "PAOT";
    }

    interface Tables {
        String MOSQUITO_COLLECTIONS_TABLE = "mosquito_collections";
        String LARVAL_DIPPINGS_TABLE = "larval_dippings";
        String PAOT_TABLE = "potential_area_of_transmission";
    }

    interface BusinessStatus {
        String NOT_VISITED = "Not Visited";
        String NOT_SPRAYED = "Not Sprayed";
        String SPRAYED = "Sprayed";
        String NOT_SPRAYABLE = "Not Sprayable";
        String COMPLETE = "Complete";
        String INCOMPLETE = "Incomplete";
        String NOT_ELIGIBLE = "Not Eligible";
        String IN_PROGRESS = "In Progress";
        // Following are for grouped structure tasks. Not synced to server
        String FAMILY_REGISTERED = "Family Registered";
        String BEDNET_DISTRIBUTED = "Bednet Distributed";
        String BLOOD_SCREENING_COMPLETE = "Blood Screening Complete";
        String PARTIALLY_SPRAYED = "Partially Sprayed";
    }

    interface BusinessStatusWrapper {
        List<String> SPRAYED = Arrays.asList(new String[]{BusinessStatus.SPRAYED, BusinessStatus.COMPLETE});
        List<String> NOT_SPRAYED = Arrays.asList(new String[]{BusinessStatus.NOT_SPRAYED, BusinessStatus.IN_PROGRESS, BusinessStatus.INCOMPLETE});
        List<String> NOT_ELIGIBLE = Arrays.asList(new String[]{BusinessStatus.NOT_SPRAYABLE, BusinessStatus.NOT_ELIGIBLE});
        List<String> NOT_VISITED = Arrays.asList(new String[]{BusinessStatus.NOT_VISITED});
    }

    interface Map {
        int MAX_SELECT_ZOOM_LEVEL = 16;
        int CLICK_SELECT_RADIUS = 24;
        String NAME_PROPERTY = "name";
    }

    interface JsonForm {

        String ENCOUNTER_TYPE = "encounter_type";

        String SPRAY_STATUS = "sprayStatus";

        String TRAP_SET_DATE = "trap_start";

        String TRAP_FOLLOW_UP_DATE = "trap_end";

        String BUSINESS_STATUS = "business_status";

        String STRUCTURE_TYPE = "structureType";

        String HEAD_OF_HOUSEHOLD = "familyHeadName";

        String STRUCTURE_PROPERTIES_TYPE = "[structure_type]";

        String NUMBER_OF_FAMILY_MEMBERS = "[num_fam_members]";

        String SPRAY_FORM = "json.form/spray_form.json";

        String MOSQUITO_COLLECTION_FORM = "json.form/mosquito_collection_form.json";

        String SPRAY_FORM_NAMIBIA = "json.form/namibia_spray_form.json";

        String SPRAY_FORM_BOTSWANA = "json.form/botswana_spray_form.json";

        String LARVAL_DIPPING_FORM = "json.form/larval_dipping_form.json";

        String ADD_STRUCTURE_FORM = "json.form/add_structure.json";

        String BEDNET_DISTRIBUTION_FORM = "json.form/bednet_distribution.json";

        String BLOOD_SCREENING_FORM = "json.form/blood_screening.json";

        String CASE_CONFIRMATION_FORM = "json.form/case_confirmation.json";

        String BEHAVIOUR_CHANGE_COMMUNICATION_FORM = "json.form/behaviour_change_communication.json";

        String PAOT_FORM = "json.form/paot.json";

        String THAILAND_LARVAL_DIPPING_FORM = "json.form/thailand_larval_dipping_form.json";

        String THAILAND_MOSQUITO_COLLECTION_FORM = "json.form/thailand_mosquito_collection_form.json";

        String THAILAND_SPRAY_FORM = "json.form/thailand_spray_form.json";

        String THAILAND_ADD_STRUCTURE_FORM = "json.form/thailand_add_structure.json";

        String THAILAND_BEDNET_DISTRIBUTION_FORM = "json.form/thailand_bednet_distribution.json";

        String THAILAND_BLOOD_SCREENING_FORM = "json.form/thailand_blood_screening.json";

        String THAILAND_CASE_CONFIRMATION_FORM = "json.form/thailand_case_confirmation.json";

        String THAILAND_BEHAVIOUR_CHANGE_COMMUNICATION_FORM = "json.form/thailand_behaviour_change_communication.json";

        String THAILAND_PAOT_FORM = "json.form/thailand_paot.json";

        String OPERATIONAL_AREA_TAG = "operational_area";

        String STRUCTURES_TAG = "structures";

        String LOCATION_COMPONENT_ACTIVE = "my_location_active";

        String NO_PADDING = "no_padding";

        String SHORTENED_HINT = "shortened_hint";

        String HINT = "hint";

        String TITLE = "title";

        String FAMILY_MEMBER = "familyMember";

        String STRUCTURE_NAME = "structureName";

        String PHYSICAL_TYPE = "physicalType";

        String PAOT_STATUS = "paotStatus";

        String PAOT_COMMENTS = "paotComments";

        String LAST_UPDATED_DATE = "lastUpdatedDate";

        String SELECTED_OPERATIONAL_AREA_NAME = "selectedOpAreaName";

        String NAMIBIA_ADD_STRUCTURE_FORM = "json.form/namibia_add_structure.json";

        String HOUSEHOLD_ACCESSIBLE = "householdAccessible";
        String ABLE_TO_SPRAY_FIRST = "ableToSprayFirst";
        String MOP_UP_VISIT = "mopUpVisit";

    }

    interface DateFormat {

        String EVENT_DATE_FORMAT_Z = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";

        String EVENT_DATE_FORMAT_XXX = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX";

        String CARD_VIEW_DATE_FORMAT = "dd MMM yyyy";
    }

    interface Action {
        String STRUCTURE_TASK_SYNCED = "reveal.STRUCTURE_TASK_SYNCED";
    }

    interface ECClientConfig {
        String NAMIBIA_EC_CLIENT_FIELDS = "ec_client_fields_namibia.json";
        String BOTSWANA_EC_CLIENT_FIELDS = "ec_client_fields_botswana.json";
    }


    interface StructureType {
        String RESIDENTIAL = "Residential Structure";

        String NON_RESIDENTIAL = "Non-Residential Structure";

        String MOSQUITO_COLLECTION_POINT = "Mosquito Collection Point";

        String LARVAL_BREEDING_SITE = "Larval Breeding Site";

        String POTENTIAL_AREA_OF_TRANSMISSION = "Potential Area of Transmission";
    }

    interface TaskRegister {
        String VIEW_IDENTIFIER = "task_register";

        String INTERVENTION_TYPE = "intervention_type";

        String LAST_USER_LOCATION = "last_location";

    }

    interface DatabaseKeys {

        String TASK_TABLE = "task";

        String SPRAYED_STRUCTURES = "sprayed_structures";

        String STRUCTURES_TABLE = "structure";

        String STRUCTURE_NAME = "structure_name";

        String STRUCTURE_ID = "structure_id";

        String ID = "_id";

        String CODE = "code";

        String FOR = "for";

        String BUSINESS_STATUS = "business_status";

        String STATUS = "status";

        String REFERENCE_REASON = "reason_reference";

        String FAMILY_NAME = "family_head_name";

        String SPRAY_STATUS = "spray_status";

        String LATITUDE = "latitude";

        String LONGITUDE = "longitude";

        String NAME = "name";

        String GROUPID = "group_id";

        String PLAN_ID = "plan_id";

        String NOT_SRAYED_REASON = "not_sprayed_reason";

        String NOT_SRAYED_OTHER_REASON = "not_sprayed_other_reason";

        String OTHER = "other";

        String COMPLETED_TASK_COUNT = "completed_task_count";

        String TASK_COUNT = "task_count";

        String BASE_ENTITY_ID = "base_entity_id";

        String FIRST_NAME = "first_name";

        String LAST_NAME = "last_name";

        String GROUPED_STRUCTURE_TASK_CODE_AND_STATUS = "grouped_structure_task_code_and_status";

        String GROUPED_TASKS = "grouped_tasks";

        String LAST_UPDATED_DATE = "last_updated_date";

        String PAOT_STATUS = "paot_status";

        String PAOT_COMMENTS = "paot_comments";

        String EVENT_TASK_TABLE = "event_task";

        String EVENT_ID = "event_id";

        String TASK_ID = "task_id";

        String EVENT_DATE = "event_date";

        String EVENTS_PER_TASK = "events_per_task";

    }

    interface PlanDefinitionStatus {
        String DRAFT = "draft";
        String ACTIVE = "active";
        String COMPLETE = "complete";
        String RETIRED = "retired";
    }

    interface UseContextCode {
        String INTERVENTION_TYPE = "interventionType";
    }

}
