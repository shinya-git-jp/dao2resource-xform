import { useSettingStore } from '../store/setting-store';
import { InputListPage } from '../features/transform-code/components/InputList';
import SettingListPage from '../features/setting/components/SettingListPage';
import { EntityInputList } from '../features/transform-entity/components/InputList';


export default function DashboardPage({ pathname }: { pathname: string }) {
  const storedSetting = localStorage.getItem('setting');
  if (storedSetting) {
    const setting = JSON.parse( storedSetting);
    const setSetting = useSettingStore((state) => state.setSetting);
    setSetting(setting);
  }
  return (
    <>
      
    </>
  );
  // if (pathname == "/settings") {
  //   return <SettingListPage/>
  // } else if (pathname == "/transform/code") {
  //   return <InputListPage/>
  // } else if (pathname == "/transform/entity") {
  //   return <EntityInputList/>
  // } else {
  //   return <InputListPage/>
  // }
}