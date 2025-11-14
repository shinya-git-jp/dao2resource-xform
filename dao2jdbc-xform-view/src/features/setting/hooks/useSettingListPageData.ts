import React from "react";
import { useSettingStore } from "../../../store/setting-store";
import { saveSettingsToBrowser } from "../../../utils/utils";
import { DbType } from "../../../types/enum";

export const useSettingListPageData = () => {
    const [openDialog, setOpenDialog] = React.useState(false);
    const [settingsChecked, setSettingsChecked] = React.useState({});
    const settings = useSettingStore((state)  => state.settings);
    const setSettings = useSettingStore((state) => state.setSettings);
    const removeSetting = useSettingStore((state)  => state.removeSetting);
    const fileInputRef = React.useRef(null);
    const addSetting = useSettingStore((state)  => state.addSetting);
    const [searchTerm, setSearchTerm] = React.useState(""); 
    const [setting, setSetting] = React.useState<Setting>({
        id: "",
        savedName: "",
        db: {
        url: "",
        dbType: DbType.ORACLE,
        schema: "",
        username: "",
        password: ""
        },
        locale: {
        current: "ja",
        mapping: {
            country1: "",
            country2: "",
            country3: "",
            country4: "",
            country5: ""
        }
        },
        reservedWords: {
        primaryKey: "",
        exclusiveKey: "",
        companyCodeKey: "",
        insertedUserIDKey: "",
        insertedDateKey: "",
        updatedUserIDKey: "",
        updatedDateKey: ""
        },
        transform: {
        encoding: "UTF-8",
        forceAliasColumn: false,
        entityQuery: true,
        useForeignKey: false,
        useExpMap: false
        },
        savedTime: ""
  });
    
    React.useEffect(()=> {
     saveSettingsToBrowser(settings);
    }, [settings])
    return {
        openDialog,
        setOpenDialog,
        settingsChecked,
        setSettingsChecked,
        settings,
        setSettings,
        removeSetting,
        setting,
        fileInputRef,
        searchTerm,
        setSearchTerm,
        addSetting,
        setSetting
    }
}