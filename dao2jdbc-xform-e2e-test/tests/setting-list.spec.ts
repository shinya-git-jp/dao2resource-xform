import { test, expect } from '@playwright/test';
import { DEFAULT_URL } from './constants';
import { SettingListPage } from './setting-list';

test.describe.configure({mode: "serial"});
test.slow();
test.beforeEach(async ({page})=> {
  await page.goto(DEFAULT_URL);
})

export const addNewSetting = async (settingListPage: SettingListPage) => {
  await settingListPage.clickNewItemButton();
  await settingListPage.settingDialog.selectDatabaseSettingsSection();
  await settingListPage.settingDialog.setUrlField("jdbc:mysql://gef-unit-mysql8027.c3cxjzdf21mz.ap-northeast-1.rds.amazonaws.com:3306/gef_jdbc_tool?zeroDateTimeBehavior=convertToNull&useUnicode=true&useSSL=false&serverTimezone=GMT%2B9");
  await settingListPage.settingDialog.setDbTypeField("MySQL");
  await settingListPage.settingDialog.setSchemaField("gef_jdbc_tool");
  await settingListPage.settingDialog.setUsernameField("geframe");
  await settingListPage.settingDialog.setPasswordField("Kccs0000");
  await settingListPage.settingDialog.selectReservedWordsSettingsSection();
  await settingListPage.settingDialog.setPrimaryKeyField("objectID");
  await settingListPage.settingDialog.setExclusiveKeyField("ExclusiveFG");
  await settingListPage.settingDialog.setCompanyCodeField("CompanyCD");
  await settingListPage.settingDialog.setInsertedUserIdField("RegisteredPerson");
  await settingListPage.settingDialog.setInsertedDateField("RegisteredDT");
  await settingListPage.settingDialog.setUpdatedUserIdField("UpdatedPerson");
  await settingListPage.settingDialog.setUpdatedDateField("UpdatedDT");
  await settingListPage.settingDialog.selectLocaleSettingsSection();
  await settingListPage.settingDialog.setLocaleField("en");
  await settingListPage.settingDialog.setCountry1Field("en");
  await settingListPage.settingDialog.setCountry2Field("jp");
  await settingListPage.settingDialog.setCountry3Field("id");
  await settingListPage.settingDialog.setCountry4Field("zh");
  await settingListPage.settingDialog.setCountry5Field("us");
  await settingListPage.settingDialog.selectTransformSettingsSection();
  await settingListPage.settingDialog.checkForceAliasColumn();
  await settingListPage.settingDialog.checkUseExpMap();
  await settingListPage.settingDialog.checkUseForeignKey();
  await settingListPage.settingDialog.clickSaveButton('test');
}

export const addNewFalseSetting = async (settingListPage: SettingListPage) => {
  await settingListPage.clickNewItemButton();
  await settingListPage.settingDialog.selectDatabaseSettingsSection();
  await settingListPage.settingDialog.setUrlField("jdbc:mysql://gef-unit-mysql8027.c3cxjzdf21mz.ap-northeast-1.rds.amazonaws.com:3306/gef_jdbc_tool?zeroDateTimeBehavior=convertToNull&useUnicode=true&useSSL=false&serverTimezone=GMT%2B9");
  await settingListPage.settingDialog.setDbTypeField("MySQL");
  await settingListPage.settingDialog.setSchemaField("gef_jdbc_tool");
  await settingListPage.settingDialog.setUsernameField("geframe2");
  await settingListPage.settingDialog.setPasswordField("Kccs0000");
  await settingListPage.settingDialog.selectReservedWordsSettingsSection();
  await settingListPage.settingDialog.setPrimaryKeyField("objectID");
  await settingListPage.settingDialog.setExclusiveKeyField("ExclusiveFG");
  await settingListPage.settingDialog.setCompanyCodeField("CompanyCD");
  await settingListPage.settingDialog.setInsertedUserIdField("RegisteredPerson");
  await settingListPage.settingDialog.setInsertedDateField("RegisteredDT");
  await settingListPage.settingDialog.setUpdatedUserIdField("UpdatedPerson");
  await settingListPage.settingDialog.setUpdatedDateField("UpdatedDT");
  await settingListPage.settingDialog.selectLocaleSettingsSection();
  await settingListPage.settingDialog.setLocaleField("en");
  await settingListPage.settingDialog.setCountry1Field("en");
  await settingListPage.settingDialog.setCountry2Field("jp");
  await settingListPage.settingDialog.setCountry3Field("id");
  await settingListPage.settingDialog.setCountry4Field("zh");
  await settingListPage.settingDialog.setCountry5Field("us");
  await settingListPage.settingDialog.selectTransformSettingsSection();
  await settingListPage.settingDialog.checkForceAliasColumn();
  await settingListPage.settingDialog.checkUseExpMap();
  await settingListPage.settingDialog.checkUseForeignKey();
  await settingListPage.settingDialog.clickSaveButton('test');
}
test('新規設定を追加した後、ページを再読み込みしても、その設定が記録に保持されていること。', async ({ page }) => {
  const settingListPage = new SettingListPage(page);
  await settingListPage.openSettingPage();
  await addNewSetting(settingListPage);
  settingListPage.loadListSettings();
  await page.reload();
  await settingListPage.openSettingPage();
  await settingListPage.loadListSettings();
  const listSettings = await settingListPage.listSettings.count();
  const listSettingCheckboxes = await settingListPage.listSettingCheckboxes.count();
  expect(listSettings).toBe(1);
  expect(listSettingCheckboxes).toBe(1);
});

test('設定を編集した後、ページを再読み込みしても、変更が記録に保持されていること。', async ({ page }) => {
  const settingListPage = new SettingListPage(page);
  await settingListPage.openSettingPage();
  settingListPage.loadListSettings();
  await addNewSetting(settingListPage);
  await settingListPage.settingDialog.clickCloseButton();
  await settingListPage.clickListSetting(0);
  await settingListPage.settingDialog.selectTransformSettingsSection();
  await settingListPage.settingDialog.uncheckForceAliasColumn();
  await settingListPage.settingDialog.clickSaveButton('test2');
  await page.reload();
  await settingListPage.openSettingPage();
  await settingListPage.loadListSettings();
  const listSettings = await settingListPage.listSettings.count();
  const listSettingCheckboxes = await settingListPage.listSettingCheckboxes.count();
  expect(listSettings).toBe(1);
  expect(listSettingCheckboxes).toBe(1);
  expect(page.getByText("test2")).toBeVisible();
  await settingListPage.clickListSetting(0);
  await settingListPage.settingDialog.selectTransformSettingsSection();
  expect(await settingListPage.settingDialog.forceAliasColumnCheckBox.isChecked()).toBe(false);
});

test("設定を削除した後、ページを再読み込みしても、その設定が記録から削除された状態が保持されていること。", async({page}) => {
  const settingListPage = new SettingListPage(page);
  await settingListPage.openSettingPage();
  settingListPage.loadListSettings();
  await addNewSetting(settingListPage);
  await settingListPage.settingDialog.clickCloseButton();
  await settingListPage.clickListSettingCheckbox(0);
  await settingListPage.clickDeleteButton();
  await page.reload();
  await settingListPage.openSettingPage();
  await settingListPage.loadListSettings();
  const listSettings = await settingListPage.listSettings.count();
  const listSettingCheckboxes = await settingListPage.listSettingCheckboxes.count();
  expect(listSettings).toBe(0);
  expect(listSettingCheckboxes).toBe(0);
})

test("設定をインポートした後、ページを再読み込みしても、インポートされた設定が記録に保持されていること。", async({page}) => {
  const settingListPage = new SettingListPage(page);
  await settingListPage.openSettingPage();
  await settingListPage.selectImportFileTemp('test-files/settings.yaml');
  await settingListPage.loadListSettings();
  let listSettings = await settingListPage.listSettings.count();
  let listSettingCheckboxes = await settingListPage.listSettingCheckboxes.count();
  expect(listSettings).toBe(2);
  expect(listSettingCheckboxes).toBe(2);
  await page.reload();
  await settingListPage.openSettingPage();
  await settingListPage.loadListSettings();
  listSettings = await settingListPage.listSettings.count();
  listSettingCheckboxes = await settingListPage.listSettingCheckboxes.count();
  expect(listSettings).toBe(2);
  expect(listSettingCheckboxes).toBe(2);
});

test("設定の接続が成功した場合、「接続成功！」と表示されること。", async({page}) => {
  const settingListPage = new SettingListPage(page);
  await settingListPage.openSettingPage();
  await settingListPage.clickNewItemButton();
  await settingListPage.settingDialog.selectDatabaseSettingsSection();
  await settingListPage.settingDialog.setUrlField("jdbc:mysql://gef-unit-mysql8027.c3cxjzdf21mz.ap-northeast-1.rds.amazonaws.com:3306/gef_jdbc_tool?zeroDateTimeBehavior=convertToNull&useUnicode=true&useSSL=false&serverTimezone=GMT%2B9");
  await settingListPage.settingDialog.setDbTypeField("MySQL");
  await settingListPage.settingDialog.setSchemaField("gef_jdbc_tool");
  await settingListPage.settingDialog.setUsernameField("geframe");
  await settingListPage.settingDialog.setPasswordField("Kccs0000");
  let alertMessage = '';
  page.on('dialog', async (dialog) => {
    alertMessage = dialog.message();
    await dialog.accept();
  });
  await settingListPage.settingDialog.clickConnectionTestButton();
  await expect(page.getByText("確認中")).toBeVisible();
  await expect(page.getByText("確認中")).toBeHidden({timeout: 1200000});
  expect(alertMessage).toBe('接続成功！');
});

test("設定の接続が失敗した場合、「接続エラー」と表示されること。", async({page}) => {
  const settingListPage = new SettingListPage(page);
  await settingListPage.openSettingPage();
  await settingListPage.clickNewItemButton();
  await settingListPage.settingDialog.selectDatabaseSettingsSection();
  await settingListPage.settingDialog.setUrlField("jdbc:mysql://gef-unit-mysql8027.c3cxjzdf21mz.ap-northeast-1.rds.amazonaws.com:3306/gef_jdbc_tool?zeroDateTimeBehavior=convertToNull&useUnicode=true&useSSL=false&serverTimezone=GMT%2B9");
  await settingListPage.settingDialog.setDbTypeField("MySQL");
  await settingListPage.settingDialog.setSchemaField("gef_jdbc_tool");
  await settingListPage.settingDialog.setUsernameField("geframe2");
  await settingListPage.settingDialog.setPasswordField("Kccs0000");
  let alertMessage = '';
  page.on('dialog', async (dialog) => {
    alertMessage = dialog.message();
    await dialog.accept();
  });
  await settingListPage.settingDialog.clickConnectionTestButton();
  await expect(page.getByText("確認中")).toBeVisible();
  await expect(page.getByText("確認中")).toBeHidden({timeout: 1200000});
  expect(alertMessage).toBe('接続エラー');
});