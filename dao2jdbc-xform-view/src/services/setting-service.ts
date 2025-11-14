import { AxiosResponse } from "axios";
import { http } from "../config/http";
// import { encryptSettingPassword } from "../utils/utils";
import { SQL_EXTRACTOR_SERVICE } from "../config/urls";
import { RequestManager } from "../utils/RequestManager";
import { v4 as uuidv4 } from 'uuid';

export const checkSettingConnection = async (setting: Setting) => {
    const signal = RequestManager.create(uuidv4());

    const response : AxiosResponse<ApiResponse<String>>= await http.post(SQL_EXTRACTOR_SERVICE+"check-connections", {
        value: {},
        setting: setting
    }, {signal});
    if (response.status == 200) {
        if (response.data.data) {
            return response.data.data == "Success!";
        }
    }    
    return false;
}