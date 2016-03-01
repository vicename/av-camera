package com.yamedie.common;

/**
 * Created by Li Dachang on 16/1/26.
 * ..-..---.-.--..---.-...-..-....-.
 */
public class CommonDefine {
    public static final boolean IS_DEBUG =true;
    public static final String IS_FIRST = "is_first";

    public static final String API_KEY = "api_key";
    public static final String API_SECRET = "api_secret";

    public static final String API_KEY_VALUE = "9ceedf0fb957b00633e812b03603592f";
    public static final String API_SECRET_VALUE = "o58YBkVsk74rVFb1sA73mzM7uiPogvTJ";

    public static final String FACE_PP_URL = "http://apicn.faceplusplus.com/v2";
    public static final String URL_FACE_SET = "/info/get_faceset_list";
    public static final String URL_SEARCH_FACE = "/recognition/search";
    public static final String URL_GET_FACE = "/info/get_face";

    public static final String FACE_PP_URL_0 = "http://apicn.faceplusplus.com/v2/detection/detect";
    public static final String FACE_URL_0 = "http://apicn.faceplusplus.com";
    public static final String IMG_STORE_URL = "http://192.168.13.57:3000/img";


    public static final String FACE_SET_NAME = "faceset_name";
    public static final String FACE_SET_NAME_VALUE = "av";

    public static final String ACTION_CAMERA_GO = "camera_go";
    public static final String ACTION_TAKE_PHOTO_TO_SHOW_IMG = "action_take_take";
    public static final String ACTION_CHOOSE_PIC_TO_SHOW_IMG = "action choose_pic";
    public static final String TAG_IMAGE_PATH = "IMAGE_PATH";//用于拍照传送图片路径
    public static final String TAG_IMAGE_URL = "IMAGE_URL";
    public static final String TAG_TEACHER_NAME = "TEACHER_NAME";
    public static final String TAG_SIMILAR = "SIMILAR";
    public static final String TAG_TIME = "TIME";
    public static final String TAG_GO_SHOW_MAP = "GO_SHOW_MAP";

    public static final String PIC_SAVE_PATH = "av-camera";
    public static final String PIC_SAVE_Path2 = "Picture/av-camera";
    public static final String PIC_TEMP_PATH="/storage/emulated/0/picture/av-camera/tempsdsss/";

    //上传图片的url
    public static final String URL_UPLOAD = "http://192.168.13.76:3000/upload";
    public static final String URL_UPLOAD_2 = "http://stg-avcam.leanapp.cn/upload";
//    public static final String

    //测试用的图片url
    public static final String URL_IMAGE_LOAD_TEST = "https://pic2.zhimg.com/82b27a292c6520c36afa29972d0673fd_b.jpg";

    /**
     * 友盟埋点
     */
    //在拍照页面拍照
    public static final String UM_TAKE_PHOTO_TAKE_PHOTO = "take_photo";
    //在拍照页面选择图片
    public static final String UM_TAKE_PHOTO_CHOOSE_PIC = "choose_pic";
    //在展示页面选择图片
    public static final String UM_SHOW_IMG_CHOOSE_PIC = "show_img_choose_pic";
    //拍照后找老师
    public static final String UM_FIND_TEACHER_ON_TAKE_PHOTO = "find_teacher_on_take_photo";
    //选择图片后找老师
    public static final String UM_FIND_TEACHER_ON_CHOOSE_PIC = "find_teacher_on_choose_pic";
    //拍照后展示老师
    public static final String UM_SHOW_TEACHER_AFTER_TAKE_PHOTO="show_teacher_after_take_photo";
    //选择图片后展示老师
    public static final String UM_SHOW_TEACHER_AFTER_CHOOSE_PIC = "show_teacher_after_choose_pic";
    //展示老师
    public static final String UM_SHOW_TEACHER = "show_teacher";
    //找老师用的时间
    public static final String UM_FIND_TEACHER_TIME = "find_teacher_time";
    //保存老师
    public static final String UM_CLICK_SAVE_TEACHER = "click_save_teacher";
    //保存老师合成图片
    public static final String UM_CLICK_SAVE_TEACHER_COMPOSED = "click_save_teacher_composed";
}
