import React from "react";
import { useInputStore } from "../../../store/entity-input-store";
import { useSettingStore } from "../../../store/setting-store";

export const useEntityInputDialogData = (initialData: EntityCondition) => {
    const setting: Setting = useSettingStore((state) => state.globalSetting);
    const conditions: Array<EntityCondition> = useInputStore((state) => state.conditions);
    const condition: EntityCondition = useInputStore((state) => state.condition);
    const setCondition = useInputStore((state) => state.setCondition);
    const settings = useSettingStore((state) => state.settings);
    const addCondition = useInputStore((state) => state.addCondition);
    const resetCondition = useInputStore((state) => state.resetCondition);
    
    React.useEffect(() => {
        setCondition(initialData);
        }, [initialData]);
    return {
        setting,
        conditions,
        condition,
        setCondition,
        settings,
        addCondition,
        resetCondition
    }
}