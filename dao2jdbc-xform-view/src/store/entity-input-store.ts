import { create } from 'zustand'

export const useInputStore = create<EntityInputStore>((set) => ({
  conditions: [],
  condition: {
    id: "0",
    description: "",
    type: "entityId",
    setting: null
  },
  setCondition: (condition) => set((state)=> ({...state, condition: condition})),
  addCondition: (newCondition) => set((state) => ({ conditions: [newCondition, ...state.conditions] })),
  removeCondition: (id) => set((state) => ({ conditions: state.conditions.filter((condition) => condition.id !== id) })),
  editCondition: (id, newCondition) => set((state) => ({ conditions: state.conditions.map((condition) => (condition.id === id ? { ...condition, ...newCondition } : condition)) })),
  resetConditions: () => set(() => ({ conditions: [] })),
  resetCondition: ()=> set((state) => ({ condition : {
        id: "0",
        description: "",
        type: "entityId",
        setting: null
      }
  }))
}))