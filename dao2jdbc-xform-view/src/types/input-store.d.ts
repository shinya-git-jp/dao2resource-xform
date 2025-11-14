
type InputStore = {
  scripts: Array<Script>;
  script: Script;
  setScript: (script: Script) => void;
  addScript: (newScript: Script) => void;
  removeScript: (id: string) => void;
  setScripts: (scripts: Script[]) => void;
  editScript: (id: string, newScript: Script) => void;
  resetScripts: () => void;
  resetScript: () => void;
};

type Script = {
  id: string;
  description: string;
  type: ScriptType;
  crudTypeSelect: boolean;
  crudTypeDelete: boolean;
  crudTypeInsert: boolean;
  crudTypeUpdate: boolean;
  setting: Setting;
};

type SqlExtractionOutput = {
  sqlScript: string;
  type: CrudType;
  virtualEntity: Map<String, Object>;
}

type Sql2CodeOutput = {
  description: string;
  sqlScript: string;
  type: string;
  apiCodes: string;
  input: Script;
  id: string;
}

type ScriptRequest = {
  veId: string;
  crudTypes: CrudType[];
}

