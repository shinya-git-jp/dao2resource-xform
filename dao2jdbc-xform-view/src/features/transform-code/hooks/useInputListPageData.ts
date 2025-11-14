import React, { useEffect, useRef } from "react";
import { useSettingStore } from "../../../store/setting-store";
import { useInputStore } from "../../../store/input-store";
import { ScriptType } from "../../../types/enum";

export const useInputListPageData = () => {
    const setGlobalSetting = useSettingStore((state)  => state.setGlobalSetting);
    const [open, setOpen] = React.useState<boolean>(false);
    const [openPreview, setOpenPreview] = React.useState<boolean>(false);
    const settings = useSettingStore((state: {settings: Array<Setting>})  => state.settings);
    const [tempGlobalSetting, setTempGlobalSetting] = React.useState<Setting>(null);
    const scripts = useInputStore((state)  => state.scripts);
    const removeScript = useInputStore((state)  => state.removeScript);
    const [scriptsChecked, setScriptsChecked] = React.useState({});
    const [searchTerm, setSearchTerm] = React.useState(""); 
    const [crudTypeTerm, setCrudTypeTerm] = React.useState("");
    const addScript = useInputStore((state) => state.addScript);
    const [script, setScript] = React.useState({
        id: "0",
        description: "",
        type: ScriptType.VEID,
        crudTypeSelect: false,
        crudTypeDelete: false,
        crudTypeInsert: false,
        crudTypeUpdate: false,
        setting: null
    });
    
    return {
        open,
        setOpen,
        openPreview,
        setOpenPreview,
        settings,
        setGlobalSetting,
        tempGlobalSetting,
        setTempGlobalSetting,
        scripts,
        setScript,
        removeScript,
        scriptsChecked,
        setScriptsChecked,
        searchTerm,
        setSearchTerm,
        crudTypeTerm,
        setCrudTypeTerm,
        script,
        addScript
    }
}