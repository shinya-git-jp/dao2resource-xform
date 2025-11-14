import * as React from 'react';
import Box from '@mui/material/Box';
import { Button, Dialog, FormControlLabel, IconButton, InputLabel, MenuItem, Radio, RadioGroup, Select, TextField, Toolbar, Typography } from '@mui/material';
import { SimpleTreeView } from '@mui/x-tree-view/SimpleTreeView';
import { checkEntityIdExistance } from '../../../services/transform-service';
import { TitleTreeItem } from '../../../utils/utils';
import { useEntityInputDialogData } from '../hooks/useEntityInputDialogData';
import { Close } from '@mui/icons-material';
import CustomLabel from '../../../components/CustomLabel';


interface InputDialogProps {
    open: boolean;
    initialScript: EntityCondition;
    onSubmit: (condition: EntityCondition) => void;
    onClose: () => void;
}

export const EntityInputDialog: React.FC<InputDialogProps> = ({open, initialScript, onSubmit, onClose}) => {
    const {
        setting,
        condition,
        setCondition,
        settings,
    } = useEntityInputDialogData(initialScript);

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
      if (setting == null && condition.setting == null) {
        alert("設定ファイルがありません。設定ファイルを選択してください。");
        return;
      }
      showLoading();
      let selectedSetting = condition.setting;
      if (selectedSetting == null) {
        selectedSetting = setting;
      }
      try {
          const isExist = await checkEntityIdExistance(condition, selectedSetting);
          hideLoading();
          if (isExist) {
          } else {
            throw new Error("IDが存在しません");
          }
      } catch (error) {
        hideLoading();
        throw new Error("IDが存在しません");
      }
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
            if (condition.description == null || condition.description == "") {
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
            onSubmit(condition);
        }}>
            
              <Box textAlign="right" marginBottom={2}>
                  <Button variant="outlined" sx={{marginTop: 3}} onClick={()=>checkIsIdExist()}>IDの検証</Button>
                  <Button variant="outlined" type="submit" sx={{marginTop: 3, marginLeft: 1}}>保存</Button>
              </Box>
              <Box textAlign="left" paddingTop="50px" sx={{border: "1px solid #c9cdd1", paddingLeft: 3, paddingRight: 3, paddingTop: 2, paddingBottom: 2, borderRadius: 5, marginTop: 3}}>
                  <Box marginTop="10px" textAlign="start">
                      <CustomLabel label="ID:" tooltipText="ID分類項目によって、変換時の対象IDです。"/>
                      <TextField
                      size="small"
                      inputRef={idInputRef}
                      error={errorPath === 'id'}
                      id="outlined-required"
                      name="description"
                      value={condition.description}
                      placeholder='ID'
                      fullWidth={true}
                      onChange={(e) => {setCondition({... condition, description: e.target.value})}}
                      />
                  </Box>
                  <Box marginTop="10px" textAlign="start">
                      <CustomLabel label="IDの分類:" tooltipText="エンティティIDを選択する場合、変換時にエンティティIDが対象になります。データベースIDを選択する場合、データベースIDの配下にあるエンティティが全部対象になります。"/>
                      <RadioGroup
                          aria-labelledby="demo-radio-buttons-group-label"
                          defaultValue="female"
                          name="radio-buttons-group"
                          value={condition.type}
                          onChange={(e) => {setCondition({...condition, type: e.target.value})}}
                          row
                      >
                          <FormControlLabel value="databaseId" control={<Radio/>} label="データベースID"/>
                          <FormControlLabel value="entityId" control={<Radio/>} label="エンティティID" />
                      </RadioGroup>
                  </Box>
              </Box>
                  {/* <Box textAlign="left" paddingTop="50px" sx={{border: "1px solid #c9cdd1", paddingLeft: 3, paddingRight: 3, paddingTop: 2, paddingBottom: 2, borderRadius: 5, marginTop: 3}}>
                    <Box marginTop="10px" textAlign="start">
                        <CustomLabel label="変換設定: " tooltipText="この変換設定を選択する場合、この変換設定が優先されます。選択されない場合、親ページにある変換設定が使用されます。"/>
                        <Select
                            size="small"
                            displayEmpty
                            data-testid="selected-setting"
                            value={condition.setting ? condition.setting.id : ""}
                            fullWidth={true}
                            onChange={(e) => {
                                let settingId: string = e.target.value;
                                  if (e.target.value == "") {
                                      settingId = null;
                                  }
                                  const tempSetting = settings.find((s: {id: string}) => s.id === settingId);
                                setCondition({...condition, setting: tempSetting})
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