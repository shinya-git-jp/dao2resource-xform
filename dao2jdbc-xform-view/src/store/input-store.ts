import { create } from 'zustand'
import { ScriptType } from '../types/enum'

export const useInputStore = create<InputStore>((set) => ({
  scripts: [],
  script: {
    id: "0",
    description: "",
    type: ScriptType.VEID,
    crudTypeSelect: false,
    crudTypeDelete: false,
    crudTypeInsert: false,
    crudTypeUpdate: false,
    setting: null
  },
  setScript: (script) => set((state)=> ({...state, script: script})),
  setScripts: (scripts) => set(() => ({scripts: scripts})),
  addScript: (newScript) => set((state) => ({ scripts: [newScript, ...state.scripts] })),
  removeScript: (id) => set((state) => ({ scripts: state.scripts.filter((script) => script.id !== id) })),
  editScript: (id, newScript) => set((state) => ({ scripts: state.scripts.map((script) => (script.id === id ? { ...script, ...newScript } : script)) })),
  resetScripts: () => set(() => ({ scripts: [] })),
  resetScript: ()=> set(() => ({ script : {
        id: "0",
        description: "",
        type: ScriptType.VEID,
        crudTypeSelect: false,
        crudTypeDelete: false,
        crudTypeInsert: false,
        crudTypeUpdate: false,
        setting: null
      }
  }))
}))