import React from "react";
import { useInputStore } from "../../../store/input-store";
import { useSettingStore } from "../../../store/setting-store";

export const useInputDialogData = (initialData: Script) => {
    const setting = useSettingStore((state) => state.globalSetting);
    const scripts = useInputStore((state) => state.scripts);
    const script = useInputStore((state) => state.script);
    const setScript = useInputStore((state) => state.setScript);
    const settings = useSettingStore((state) => state.settings);
    const addScript = useInputStore((state) => state.addScript);
    const resetScript = useInputStore((state) => state.resetScript);

    React.useEffect(() => {
        setScript(initialData);
    }, [initialData]);
    return {
        setting,
        scripts,
        script,
        setScript,
        settings,
        addScript,
        resetScript
    }
}