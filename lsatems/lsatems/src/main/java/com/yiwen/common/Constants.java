package com.yiwen.common;

/**
 * (一句话功能描述)
 * (功能详细描述)
 *
 * @author yiwen
 * @version 1.0, 2023-03-15
 * @see Constants
 **/
public class Constants
{
    public static final String DICT_TYPE_ICON = "icon";
    public static final String DICT_TYPE_SCORE = "score";
    public static final String MEG_TYPE_LAB_APPLY = "lab_apply";
    public static final String MEG_TYPE_TEAM_APPLY = "team_apply";
    public static final String MEG_TYPE_TEAM_PRESS = "team_press";
    public static final String MEG_TYPE_PLAY_ANNOUNCE = "play_announce";
    public static final String MEG_TYPE_FEEDBACK = "feedback";
    public static final String MEG_TYPE_TEAM_CERTIFICATION_SCORE = "team_certification_score";

    public static final String CHECK_PASS = "2";
    public static final String CHECK_NO_PASS = "1";
    public static final String CHECK_APPLYING = "0";

    public static final String STATUS_TEAM_CONVENE = "1";
    public static final String STATUS_TEAM_PLAYING = "2";
    public static final String STATUS_TEAM_SCORE_AUDITING = "3";
    public static final String STATUS_TEAM_FINISH = "4";
    public static final String STATUS_TEAM_SCORE_AUDIT_SUCCESS = "5";
    public static final String STATUS_TEAM_SCORE_AUDIT_FAIL = "6";

    public static final String MESSAGE_TYPE_SUCCESS = "success";
    public static final String MESSAGE_TYPE_WARNING = "warning";
    public static final String MESSAGE_TYPE_INFO = "info";
    public static final String MESSAGE_TYPE_ERROR = "error";

    public static final String FRONT__PATH_MESSAGE = "/message";

    public static final String ROLE_ADMIN = "2";
    public static final String ROLE_TEACHER = "1";
    public static final String ROLE_STUDENT = "0";


    public static final String TEAM_ROLE_CREATOR = "2";
    public static final String TEAM_ROLE_ADMIN = "1";
    public static final String TEAM_ROLE_COMMON = "0";

    public static final String REDIS_KEY_ACCOUNT_ID = "ACCOUNT_ID:";
    public static final String REDIS_KEY_USER_DETAIL_ID = "USER_DETAIL_ID:";

    public static final String REDIS_KEY_UPDATE = "UPDATE";
    public static final String REDIS_KEY_DELETE = "DELETE";
}
