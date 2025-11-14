import React from "react";
import { useSettingStore } from "../../../store/setting-store";
import { useInputStore } from "../../../store/entity-input-store";

export const useEntityInputListPageData = () => {
    const setGlobalSetting = useSettingStore((state: {setGlobalSetting: any})  => state.setGlobalSetting);
    const [open, setOpen] = React.useState(false);
    const [openPreview, setOpenPreview] = React.useState(false);
    const settings = useSettingStore((state: {settings: any})  => state.settings);
    const [tempGlobalSetting, setTempGlobalSetting] = React.useState<Setting>(null);
    const conditions = useInputStore((state)  => state.conditions);
    const setCondition = useInputStore((state)  => state.setCondition);
    const addCondition = useInputStore((state)  => state.addCondition);
    const removeCondition = useInputStore((state: {removeCondition: any})  => state.removeCondition);
    const [scriptsChecked, setScriptsChecked] = React.useState({});
    const condition: EntityCondition = useInputStore((state) => state.condition);
    

    return {
        open,
        setOpen,
        openPreview,
        setOpenPreview,
        settings,
        setGlobalSetting,
        tempGlobalSetting,
        setTempGlobalSetting,
        condition,
        conditions,
        setCondition,
        addCondition,
        removeCondition,
        scriptsChecked,
        setScriptsChecked
    }
}