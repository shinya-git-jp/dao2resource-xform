
type EntityInputStore = {
  conditions: Array<EntityCondition>;
  condition: EntityCondition;
  setCondition: (entityCondition: EntityCondition) => void;
  addCondition: (newEntityCondition: EntityCondition) => void;
  removeCondition: (id: string) => void;
  editCondition: (id: string, newScript: Script) => void;
  resetConditions: () => void;
  resetCondition: () => void;
};

type EntityCondition = {
  id: string;
  description: string;
  type: EntityConditionType;
  setting: Setting;
};

type ExtractEntityRequest = {
    entityId: string;
    type: EntityConditionType;
}