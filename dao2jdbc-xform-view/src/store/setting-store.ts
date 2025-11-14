import { create } from 'zustand'
import { DbType } from '../types/enum'

export const useSettingStore = create<SettingStore>((set) => ({
  globalSetting: null,
  settings: [],
  setting: {
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
    condition: {
      encoding: "UTF-8"
    },
    transform: {
      encoding: "UTF-8",
      forceAliasColumn: false,
      entityQuery: true,
      useForeignKey: false,
      useExpMap: false
    },
    savedTime: ""
  },
  setGlobalSetting: (globalSetting) => set(() => ({ globalSetting: globalSetting })),
  removeSetting: (id) => set((state) => ({ settings: state.settings.filter((setting) => setting.id !== id) })),
  editSetting: (id, newSetting) => set((state) => ({ settings: state.settings.map((setting) => (setting.id === id ? { ...setting, ...newSetting } : setting)) })),
  addSetting: (newSetting) => set((state) => ({ settings: [newSetting, ...state.settings] })),
  setSettings: (settings) => set(() => ({ settings: settings })),
  setSetting: (setting) => set(() => ({ setting: setting })),
  setId: (id) => set((state) => ({setting: {...state.setting, id: id}})),
  setSavedName: (savedName) => set((state) => ({setting: {...state.setting, savedName: savedName}})),
  setUrl: (url) => set((state) => ({ setting: { ...state.setting, db: { ...state.setting.db, url: url } } })),
  setDbType: (dbType) => set((state) => ({ setting: { ...state.setting, db: { ...state.setting.db, dbType: dbType } } })),
  setSchema: (schema) => set((state) => ({ setting: { ...state.setting, db: { ...state.setting.db, schema: schema } } })),
  setUsername: (username) => set((state) => ({ setting: { ...state.setting, db: { ...state.setting.db, username: username } } })),
  setPassword: (password) => set((state) => ({ setting: { ...state.setting, db: { ...state.setting.db, password: password } } })),
  setCurrentLocale: (current) => set((state) => ({ setting: { ...state.setting, locale: { ...state.setting.locale, current: current } } })),
  setCountry1: (country1) => set((state) => ({ setting: { ...state.setting, locale: { ...state.setting.locale, mapping: { ...state.setting.locale.mapping, country1: country1 } } } })),
  setCountry2: (country2) => set((state) => ({ setting: { ...state.setting, locale: { ...state.setting.locale, mapping: { ...state.setting.locale.mapping, country2: country2 } } } })),
  setCountry3: (country3) => set((state) => ({ setting: { ...state.setting, locale: { ...state.setting.locale, mapping: { ...state.setting.locale.mapping, country3: country3 } } } })),
  setCountry4: (country4) => set((state) => ({ setting: { ...state.setting, locale: { ...state.setting.locale, mapping: { ...state.setting.locale.mapping, country4: country4 } } } })),
  setCountry5: (country5) => set((state) => ({ setting: { ...state.setting, locale: { ...state.setting.locale, mapping: { ...state.setting.locale.mapping, country5: country5 } } } })),
  setPrimaryKey: (primaryKey) => set((state) => ({ setting: { ...state.setting, reservedWords: { ...state.setting.reservedWords, primaryKey: primaryKey } } })),
  setExclusiveKey: (exclusiveKey) => set((state) => ({ setting: { ...state.setting, reservedWords: { ...state.setting.reservedWords, exclusiveKey: exclusiveKey } } })),
  setCompanyCodeKey: (companyCodeKey) => set((state) => ({ setting: { ...state.setting, reservedWords: { ...state.setting.reservedWords, companyCodeKey: companyCodeKey } } })),
  setInsertedUserIDKey: (insertedUserIDKey) => set((state) => ({ setting: { ...state.setting, reservedWords: { ...state.setting.reservedWords, insertedUserIDKey: insertedUserIDKey } } })),
  setInsertedDateKey: (insertedDateKey) => set((state) => ({ setting: { ...state.setting, reservedWords: { ...state.setting.reservedWords, insertedDateKey: insertedDateKey } } })),
  setUpdatedUserIDKey: (updatedUserIDKey) => set((state) => ({ setting: { ...state.setting, reservedWords: { ...state.setting.reservedWords, updatedUserIDKey: updatedUserIDKey } } })),
  setUpdatedDateKey: (updatedDateKey) => set((state) => ({ setting: { ...state.setting, reservedWords: { ...state.setting.reservedWords, updatedDateKey: updatedDateKey } } })),
  setTransformEncoding: (encoding) => set((state) => ({ setting: { ...state.setting, transform: { ...state.setting.transform, encoding: encoding }} })),
  setForceAliasColumn: (forceAliasColumn) => set((state) => ({ setting: { ...state.setting, transform: {...state.setting.transform, forceAliasColumn: forceAliasColumn  }}})),
  setEntityQuery: (entityQuery) => set((state) => ({ setting: { ...state.setting, transform: {...state.setting.transform,  entityQuery: entityQuery }}})),
  setUseForeignKey: (useForeignKey) => set((state) => ({ setting: { ...state.setting, transform: {...state.setting.transform,  useForeignKey: useForeignKey }}})),
  setUseExpMap: (useExpMap) => set((state) => ({ setting: { ...state.setting, transform: {...state.setting.transform,  useExpMap: useExpMap }}})),
  resetSetting: () => set(() => ({ setting: {
    id: "",
    savedName: "",
    db: {
      url: "",
      dbType: "oracle",
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
    condition: {
      encoding: "UTF-8"
    },
    transform: {
      encoding: "UTF-8",
      forceAliasColumn: false,
      entityQuery: true,
      useForeignKey: false,
      useExpMap: false
    },
    savedTime: ""
  }})),
  resetSettings: () => set(() => ({ settings: []}))
}))