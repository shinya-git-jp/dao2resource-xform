import { AxiosResponse } from "axios";
import { http } from "../config/http";
import { ENTITY2XML_SERVICE, ENTITY_EXTRACTOR_SERVICE, SQL2CODE_SERVICE, SQL_EXTRACTOR_SERVICE } from "../config/urls";
// import { encryptSettingPassword } from "../utils/utils";
import { v4 as uuidv4 } from 'uuid';
import { RequestManager } from "../utils/RequestManager";

export const checkVeIdExistance = async (script: Script, setting: Setting) => {
    // const tempSetting = encryptSettingPassword(setting);
    const signal = RequestManager.create(uuidv4());
    const isExist: AxiosResponse<ApiResponse<String>> = await http.post(SQL_EXTRACTOR_SERVICE+"codes/ids", {
        value:  {
            id: script.description,
            type: script.type
        },
        setting: setting},
        {signal});
    if (isExist.status == 200) {
        if (isExist.data.data) {
            return isExist.data.data == "ID is exist.";
        }
    }
    return false;
    
}

export const fetchVeIdByCategoryId = async (categoryId: string, setting: Setting) => {
    // const tempSetting = encryptSettingPassword(setting);
    const signal = RequestManager.create(uuidv4());
     const response: AxiosResponse<ApiResponse<String[]>> = await http.post(SQL_EXTRACTOR_SERVICE+"codes/categories/children", {
        value:  {
            "categoryId": categoryId
        },
        setting: setting},
        {signal}
    );
    if (response.status == 200) {
        if (response.data.data) {
            return response.data.data;
        }
    }
    return [];
}

export const extractSql = async (input: ScriptRequest, setting: Setting) => {
    // const tempSetting = encryptSettingPassword(setting);
    const signal = RequestManager.create(uuidv4());
    const response: AxiosResponse<ApiResponse<SqlExtractionOutput>> = await http.post(SQL_EXTRACTOR_SERVICE+"codes/transformations/sql-extractors", {
        value:  input,
        setting: setting},
        {signal}
    );
    if (response.status == 200) {
        if (response.data.data) {
            return response.data.data[0];
        }
    }
    return null;
}

export const convertSql2Code = async (input: SqlExtractionOutput, setting: Setting) => {
    // const tempSetting = encryptSettingPassword(setting);
    const signal = RequestManager.create(uuidv4());
    const response: AxiosResponse<ApiResponse<Sql2CodeOutput>> = await http.post(SQL2CODE_SERVICE+"codes/transformations/sql2code", {
        value:  input,
        setting: setting},
        {signal}
    );
    if (response.status == 200) {
        if (response.data.data) {
            return response.data.data;
        }
    }
    return null;
}

export const checkEntityIdExistance = async (entityCondition: EntityCondition, setting: Setting) => {
    // const tempSetting = encryptSettingPassword(setting);
    const signal = RequestManager.create(uuidv4());
    const response: AxiosResponse<ApiResponse<String>> = await http.post(ENTITY_EXTRACTOR_SERVICE+"entities/ids", {
        value:  {
            id: entityCondition.description,
            type: entityCondition.type
        },
        setting: setting},
        {signal}
    );
    if (response.status == 200) {
        if (response.data.data) {
            return response.data.data == "ID is exist";
        }
    }
    return false;
}

export const fetchEntityIdByDatabaseId = async (databaseId: string, setting: Setting) => {
    // const tempSetting = encryptSettingPassword(setting);
    const signal = RequestManager.create(uuidv4());
    const response: AxiosResponse<ApiResponse<String[]>> = await http.post(ENTITY_EXTRACTOR_SERVICE+"entities/databases/children", {
        value:  {
            "databaseId": databaseId
        },
        setting: setting},
        {signal}
    );
    if (response.status == 200) {
        if (response.data.data) {
            return response.data.data;
        }
    }
    return [];
}

export const extractEntity = async (input: ExtractEntityRequest, setting: Setting) => {
    // const tempSetting = encryptSettingPassword(setting);
    const signal = RequestManager.create(uuidv4());
    const response: AxiosResponse<ApiResponse<Map<String, Object>>> = await http.post(ENTITY_EXTRACTOR_SERVICE+"entities/transformations/entity-extractors", {
        value:  input,
        setting: setting},
        {signal}
    );
    if (response.status == 200) {
        if (response.data.data) {
            return response.data.data;
        }
    }
    return null;
}

export const convertEntity2Xml = async (input: Map<String, Object>, setting: Setting) => {
    // const tempSetting = encryptSettingPassword(setting);
    const signal = RequestManager.create(uuidv4());
    const response: AxiosResponse<ApiResponse<string>> = await http.post(ENTITY2XML_SERVICE+"entities/transformations/entity2xml", {
        value:  input,
        setting: setting},
        {signal}
    );
    if (response.status == 200) {
        if (response.data) {
            return response.data.data;
        }
    }
    return null;
}