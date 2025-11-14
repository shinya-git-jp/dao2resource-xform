import React from "react";
import { useSettingStore } from "../../../store/setting-store";
import { saveSettingsToBrowser } from "../../../utils/utils";
import { DbType } from "../../../types/enum";

const useSettingDialogData = (initialData: Setting) => {
   const [setting, setSetting] = React.useState<Setting>(initialData);
   const setId = (id: string) => {
        setSetting({
            ...setting,
            id: id
        });
    };
    const setSavedName = (savedName: string) => {
        setSetting({
            ...setting,
            savedName: savedName
        });
    };
    const setTransformEncoding = (transformEncoding: string) => {
        setSetting({
            ...setting,
            transform: {
                ...setting.transform,
                encoding: transformEncoding
            }
        });
    };
    const setForceAliasColumn = (forceAliasColumn: boolean) => {
        setSetting({
            ...setting,
            transform: {
                ...setting.transform,
                forceAliasColumn: forceAliasColumn
            }
        });
    };
    const setUseForeignKey = (useForeignKey: boolean) => {
        setSetting({
            ...setting,
            transform: {
                ...setting.transform,
                useForeignKey: useForeignKey
            }
        });
    };
    const setUseExpMap = (useExpMap: boolean) => {
        setSetting({
            ...setting,
            transform: {
                ...setting.transform,
                useExpMap: useExpMap
            }
        });
    };
    const setEntityQuery = (entityQuery: boolean) => {
        setSetting({
            ...setting,
            transform: {
                ...setting.transform,
                entityQuery: entityQuery
            }
        });
    };
    const setUrl = (url: string) => {
        setSetting({
            ...setting,
            db: {
                ...setting.db,
                url: url
            }
        });
    };
    const setDbType = (dbType: DbType) => {
        setSetting({
            ...setting,
            db: {
                ...setting.db,
                dbType: dbType
            }
        });
    };
    const setSchema = (schema: string) => {
        setSetting({
            ...setting,
            db: {
                ...setting.db,
                schema: schema
            }
        });
    };
    const setUsername = (username: string) => {
        setSetting({
            ...setting,
            db: {
                ...setting.db,
                username: username
            }
        });
    };
    const setPassword = (password: string) => {
        setSetting({
            ...setting,
            db: {
                ...setting.db,
                password: password
            }
        });
    };
    const setCurrentLocale = (currentLocale: string) => {
        setSetting({
            ...setting,
            locale: {
                ...setting.locale,
                current: currentLocale
            }
        });
    };
    const setCountry1 = (country1: string) => {
        setSetting({
            ...setting,
            locale: {
                ...setting.locale,
                mapping: {
                    ...setting.locale.mapping,
                    country1: country1
                }
            }
        });
    };
    const setCountry2 = (country2: string) => {
        setSetting({
            ...setting,
            locale: {
                ...setting.locale,
                mapping: {
                    ...setting.locale.mapping,
                    country2: country2
                }
            }
        });
    };
    const setCountry3 = (country3: string) => {
        setSetting({
            ...setting,
            locale: {
                ...setting.locale,
                mapping: {
                    ...setting.locale.mapping,
                    country3: country3
                }
            }
        });
    };
    const setCountry4 = (country4: string) => {
        setSetting({
            ...setting,
            locale: {
                ...setting.locale,
                mapping: {
                    ...setting.locale.mapping,
                    country4: country4
                }
            }
        });
    };
    const setCountry5 = (country5: string) => {
        setSetting({
            ...setting,
            locale: {
                ...setting.locale,
                mapping: {
                    ...setting.locale.mapping,
                    country5: country5
                }
            }
        });
    };
    const setPrimaryKey = (primaryKey: string) => {
        setSetting({
            ...setting,
            reservedWords: {
                ...setting.reservedWords,
                primaryKey: primaryKey
            }
        });
    };
    const setExclusiveKey = (exclusiveKey: string) => {
        setSetting({
            ...setting,
            reservedWords: {
                ...setting.reservedWords,
                exclusiveKey: exclusiveKey
            }
        });
    };
    const setCompanyCodeKey = (companyCodeKey: string) => {
        setSetting({
            ...setting,
            reservedWords: {
                ...setting.reservedWords,
                companyCodeKey: companyCodeKey
            }
        });
    };
    const setInsertedUserIDKey = (insertedUserIDKey: string) => {
        setSetting({
            ...setting,
            reservedWords: {
                ...setting.reservedWords,
                insertedUserIDKey: insertedUserIDKey
            }
        });
    };
    const setInsertedDateKey = (insertedDateKey: string) => {
        setSetting({
            ...setting,
            reservedWords: {
                ...setting.reservedWords,
                insertedDateKey: insertedDateKey
            }
        });
    };
    const setUpdatedUserIDKey = (updatedUserIDKey: string) => {
        setSetting({
            ...setting,
            reservedWords: {
                ...setting.reservedWords,
                updatedUserIDKey: updatedUserIDKey
            }
        });
    };
    const setUpdatedDateKey = (updatedDateKey: string) => {
        setSetting({
            ...setting,
            reservedWords: {
                ...setting.reservedWords,
                updatedDateKey: updatedDateKey
            }
        });
    };
    
    const [showPassword, setShowPassword] = React.useState(false);
    const handleClickShowPassword = () => {
        setShowPassword((prev) => !prev);
    };
    const settings = useSettingStore((state: {settings: any})  => state.settings);

    React.useEffect(() => {
        saveSettingsToBrowser(settings);
    }, [settings]);

    React.useEffect(() => {
        setSetting(initialData);
    }, [initialData]);


  return {
    settings,
    setting,
    setId,
    showPassword,
    setShowPassword,
    handleClickShowPassword,
    setSavedName,
    setUrl,
    setDbType,
    setSchema,
    setUsername,
    setPassword,
    setCurrentLocale,
    setCountry1,
    setCountry2,
    setCountry3,
    setCountry4,
    setCountry5,
    setPrimaryKey,
    setExclusiveKey,
    setCompanyCodeKey,
    setInsertedUserIDKey,
    setInsertedDateKey,
    setUpdatedUserIDKey,
    setUpdatedDateKey,
    setTransformEncoding,
    setForceAliasColumn,
    setUseForeignKey,
    setUseExpMap,
    setEntityQuery,
    setSetting
  }
}

export default useSettingDialogData;