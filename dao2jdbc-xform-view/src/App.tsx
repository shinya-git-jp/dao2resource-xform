import * as React from 'react';
import { useSettingStore } from './store/setting-store';
import { MyRouter } from './router';

const steps = [
  {
    label: 'Setting',
    description: `設定`,
  },
  {
    label: 'Input',
    description:
      'インプット',
  },
  {
    label: 'Output',
    description: `アウトプット`,
  },
];

export default function FullBorderedGrid() {
  const storedSetting = localStorage.getItem('settings');
  if (storedSetting) {
    const setting = JSON.parse( storedSetting);
    const setSettings = useSettingStore((state: {setSettings: any})  => state.setSettings);
    setSettings(setting);
  }
  
  React.useEffect(() => {
    if (window.location.pathname !== '/' && storedSetting == undefined) {
      window.location.href = '/'; 
    }
  }, []);
  
  return (
    <>
      <MyRouter/>
    </>
  );
}