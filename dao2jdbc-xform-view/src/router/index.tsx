
import { BrowserRouter, Route, Routes } from "react-router-dom";

import { useState } from "react";
import SettingListPage from "../features/setting/components/SettingListPage";
import InputListPage from "../features/transform-code/components/InputList";
import { EntityInputList } from "../features/transform-entity/components/InputList";
import MainPage from "../pages/MainPage";
import HomePage from "../pages/HomePage";


/**
 *
 * @create 2024/04/30
 * @author dhiaz chebastian
 * @since 24.4.0
 */
function NavigateFunctionComponent() {
  const [ran, setRan] = useState(false);

  if (!ran) {
    setRan(true);
  }
  return <></>;
}

/**
 *
 * @create 2024/04/30
 * @author dhiaz chebastian
 * @since 24.4.0
 */
export const MyRouter = () => {
  return (
    <BrowserRouter>
      {<NavigateFunctionComponent />}
      <Routes>
        <Route path="/" element={<MainPage />}>
          <Route path="" element={<HomePage />} />
          <Route path="setting" element={<SettingListPage />} />
          <Route path="transform-code" element={<InputListPage />} />
          <Route path="transform-entity" element={<EntityInputList />} />
        </Route>
      </Routes>
    </BrowserRouter>
  );
};