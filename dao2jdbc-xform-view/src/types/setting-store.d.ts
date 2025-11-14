type SettingStore = {
  globalSetting: Setting | null;
  settings: Array<Setting>;
  setting: Setting;
  setGlobalSetting: (globalSetting: Setting) => void;
  removeSetting: (id: string) => void;
  editSetting: (id: string, newSetting: Setting) => void;
  addSetting: (newSetting: Setting) => void;
  setSettings: (settings: Array<Setting>) => void;
  setSetting: (setting: Setting) => void;
  setId: (id: string) => void;
  setSavedName: (savedName: string) => void;
  setUrl: (url: string) => void;
  setDbType: (dbType: DbType) => void;
  setSchema: (schema: string) => void;
  setUsername: (username: string) => void;
  setPassword: (password: string) => void;
  setCurrentLocale: (locale: string) => void;
  setCountry1: (country1: string) => void;
  setCountry2: (country2: string) => void;
  setCountry3: (country3: string) => void;
  setCountry4: (country4: string) => void;
  setCountry5: (country5: string) => void;
  setPrimaryKey: (primaryKey: string) => void;
  setExclusiveKey: (exclusiveKey: string) => void;
  setCompanyCodeKey: (companyCodeKey: string) => void;
  setInsertedUserIDKey: (insertedUserIDKey: string) => void;
  setInsertedDateKey: (insertedDateKey: string) => void;
  setUpdatedUserIDKey: (updatedUserIDKey: string) => void;
  setUpdatedDateKey: (updatedDateKey: string) => void
  setTransformEncoding: (encoding: string) => void;
  setForceAliasColumn: (forceAliasColumn: boolean) => void;
  setEntityQuery: (entityQuery: boolean) => void;
  setUseForeignKey: (useForeignKey: boolean) => void;
  setUseExpMap: (useExpMap: boolean) => void;
  resetSetting: () => void;
  resetSettings: () => void;
};
type Setting = {
  id: string;
  savedName: string;
  db: {
    url: string;
    dbType: DbType;
    schema: string;
    username: string;
    password: string;
  };
  locale: {
    current: string,
    mapping: {
      country1: string;
      country2: string;
      country3: string;
      country4: string;
      country5: string;
    }
  };
  reservedWords: {
    primaryKey: string;
    exclusiveKey: string;
    companyCodeKey: string;
    insertedUserIDKey: string;
    insertedDateKey: string;
    updatedUserIDKey: string;
    updatedDateKey: string;
  };
  transform: {
    encoding: string;
    forceAliasColumn: boolean;
    entityQuery: boolean;
    useForeignKey: boolean;
    useExpMap: boolean;
  };
  savedTime: string;
}