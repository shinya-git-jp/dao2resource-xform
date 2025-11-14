import Box from '@mui/material/Box';
import { Button, Checkbox, Dialog, FormControlLabel, FormGroup, IconButton, InputLabel, MenuItem, Radio, RadioGroup, Select, TextField, Toolbar, Typography } from '@mui/material';
import { SimpleTreeView } from '@mui/x-tree-view/SimpleTreeView';
import { TitleTreeItem } from '../../../utils/utils';
import { useInputDialogData } from '../hooks/useInputDialogData';
import { Close } from '@mui/icons-material';
import { checkVeIdExistance } from '../../../services/transform-service';
import CustomLabel from '../../../components/CustomLabel';
import { ScriptType } from '../../../types/enum';
import React from 'react';
import { checkSettingConnection } from '../../../services/setting-service';

interface InputDialogProps {
    open: boolean;
    initialScript: Script;
    onSubmit: (script: Script) => void;
    onClose: () => void;
}

export const InputDialog: React.FC<InputDialogProps> = ({open, initialScript, onSubmit, onClose}) => {
  const {
    setting,
    script,
    setScript,
    settings
  } = useInputDialogData(initialScript);
    const onCloseDialog = ()=> {
      setErrorPath("");
      onClose();
    }
    const showLoading = () => {
      const overlay = document.getElementById('loadingOverlay2');
      if (overlay) {
          overlay.style.display = 'flex';
      }
    }
    const hideLoading = () => {
      const overlay = document.getElementById('loadingOverlay2');
      if (overlay) {
          overlay.style.display = 'none';
      }
  }
  const checkId = async () => {
    if (setting == null && script.setting == null) {
      alert("設定ファイルがありません。設定ファイルを選択してください。");
      return;
    }
    showLoading();
    let selectedSetting = script.setting;
    if (selectedSetting == null) {
      selectedSetting = setting;
    }
    try {
        const isExist = await checkVeIdExistance(script, selectedSetting);
        hideLoading();
        if (isExist) {
        } else {
          throw new Error("IDが存在しません");
        }
    } catch (error) {
      hideLoading();
      throw new Error("IDが存在しません");
    }
    hideLoading();
  }
  const checkIsIdExist = async () => {
    try {
      await checkId();
      alert("IDが存在します。")
    } catch (error) {
      alert("IDが存在しません")
    }
  }
  const idInputRef = React.useRef<HTMLInputElement>(null);
  const [errorPath, setErrorPath] = React.useState<String>("");
  return (
    <Dialog
      fullScreen
      open={open}
      onClose={onCloseDialog}
    >
      <Box sx={{ position: 'relative', backgroundColor: "white", color: "#1b85d1", borderBottom: "solid", borderBottomWidth: "0.5px", borderBottomColor: "black"}}>
        <Toolbar>
            <IconButton
              edge="start"
              color="inherit"
              onClick={onCloseDialog}
              aria-label="close"
            >
              <Close />
            </IconButton>
            <Typography sx={{ ml: 2, flex: 1 }} variant="h6" fontWeight="bold" component="div">
              変換条件の新規作成
            </Typography>
        </Toolbar>
      </Box>
      
      <Box padding="30px" component={"form"} onSubmit={async (e)=> {
          e.preventDefault();
          if (script.description == null || script.description == "") {
            setErrorPath("id");
            idInputRef.current.focus();
            alert("「ID」は必須項目です。");
            return;
          }
          setErrorPath("");
          try {
            await checkId();
          } catch (error) {
            alert("IDが存在しません。保存できません。");
            return;
          }
          onSubmit({
            id: script.id,
            description: script.description,
            type: script.type,
            crudTypeSelect: script.crudTypeSelect,
            crudTypeDelete: script.crudTypeDelete,
            crudTypeInsert: script.crudTypeInsert,
            crudTypeUpdate: script.crudTypeUpdate,
            setting: script.setting
          });
      }}>
          
          <Box textAlign="right" marginBottom={2}>
              <Button variant="outlined" sx={{marginTop: 3}} onClick={()=>checkIsIdExist()}>IDの検証</Button>
              <Button variant="outlined" type="submit" sx={{marginTop: 3, marginLeft: 1}}>保存</Button>
          </Box>
          <Box textAlign="left" paddingTop="50px" sx={{border: "1px solid #c9cdd1", paddingLeft: 3, paddingRight: 3, paddingTop: 2, paddingBottom: 2, borderRadius: 5, marginTop: 3}}>
              <Box marginTop="10px" textAlign="start">
                  <CustomLabel label="ID: " tooltipText="ID分類項目によって、変換時の対象IDです。"/>
                  <TextField
                  inputRef={idInputRef}
                  error={errorPath === 'id'}
                  size="small"
                  id="outlined-required"
                  name="description"
                  value={script.description}
                  placeholder='ID'
                  fullWidth={true}
                  onChange={(e) => {setScript({... script, description: e.target.value})}}
                  />
              </Box>
              <Box marginTop="10px" textAlign="start">
                <CustomLabel label="ID分類: " tooltipText="仮想表IDを選択する場合、変換時に仮想表IDが対象になります。カテゴリIDを選択する場合、カテゴリIDの配下にある仮想表が全部対象になります。"/>
                  <RadioGroup
                      aria-labelledby="demo-radio-buttons-group-label"
                      defaultValue="female"
                      name="radio-buttons-group"
                      value={script.type}
                      onChange={(e) => {setScript({...script, type: e.target.value})}}
                      row
                  >
                      <FormControlLabel value={ScriptType.CATEGORYID} control={<Radio/>} label="カテゴリID"/>
                      <FormControlLabel value={ScriptType.VEID} control={<Radio/>} label="仮想表ID" />
                  </RadioGroup>
              </Box>
              <Box marginTop="10px" textAlign="start">
                  <CustomLabel label="CRUDタイプ: " tooltipText="変換されたいSQL文です。どれもチェックされない場合、全部対象になります。"/>
                  <FormGroup row>
                      <FormControlLabel control={<Checkbox checked={script.crudTypeSelect} onChange={(e)=> {setScript({...script, crudTypeSelect: e.target.checked})}} name='statementTypeSelect'/>} label="SELECT"/>
                      <FormControlLabel control={<Checkbox checked={script.crudTypeDelete} onChange={(e)=> {setScript({...script, crudTypeDelete: e.target.checked})}} name='statementTypeDelete'/>} label="DELETE" />
                      <FormControlLabel control={<Checkbox checked={script.crudTypeUpdate} onChange={(e)=> {setScript({...script, crudTypeUpdate: e.target.checked})}} name='statementTypeUpdate'/>} label="UPDATE"/>
                      <FormControlLabel control={<Checkbox checked={script.crudTypeInsert} onChange={(e)=> {setScript({...script, crudTypeInsert: e.target.checked})}} name='statementTypeInsert'/>} label="INSERT" />
                  </FormGroup>
              </Box>
          </Box>
          {/* <Box textAlign="left" paddingTop="50px" sx={{border: "1px solid #c9cdd1", paddingLeft: 3, paddingRight: 3, paddingTop: 2, paddingBottom: 2, borderRadius: 5, marginTop: 3}}>
              <Box textAlign="start">
                <CustomLabel label="変換設定: " tooltipText="この変換設定を選択する場合、この変換設定が優先されます。選択されない場合、親ページにある変換設定が使用されます。"/>
                <Select
                    size="small"
                    displayEmpty
                    data-testid="selected-setting"
                    value={script.setting ? script.setting.id : ""}
                    fullWidth={true}
                    onChange={(e) => {
                          let settingId: string = e.target.value;
                          if (e.target.value == "") {
                              settingId = null;
                          }
                          const tempSetting = settings.find((s: {id: string}) => s.id === settingId);
                        setScript({...script, setting: {...tempSetting}});
                    }}
                >
                    <MenuItem  value="">
                        <em>設定を選んでください</em>
                    </MenuItem>
                    {settings.map((item: any) => (
                    <MenuItem key={item.id} value={item.id}>
                        {item.savedName}
                    </MenuItem>
                    ))}
                </Select>
            </Box>
              </Box> */}
      </Box>
    </Dialog>
  );
}
export default InputDialog;