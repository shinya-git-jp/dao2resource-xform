import { Locator, Page } from "@playwright/test";
import { SettingDialogPage } from "./setting-dialog";

export class SettingListPage {
    readonly page: Page;
    readonly importButton: Locator;
    readonly newItemButton: Locator;
    readonly deleteButton: Locator;
    listSettings: Locator;
    readonly importFileTemp: Locator;
    listSettingCheckboxes: Locator;
    readonly settingDialog: SettingDialogPage;
    readonly actionButton: Locator;

    constructor(page: Page) {
        this.page = page;
        this.importButton = page.getByRole('button', { name: 'インポート' });
        this.newItemButton = page.getByRole('button', { name: '新規作成' });
        this.deleteButton = page.getByRole('menuitem', { name: '削除' });
        this.settingDialog = new SettingDialogPage(page);
        this.actionButton = page.getByRole('button', { name: 'アクション' });
    }

    async openSettingPage() {
        await this.page.getByRole('button', { name: 'open drawer' }).click();
        await this.page.getByRole('button', { name: '変換設定の管理 変換を実行するための設定を管理します。' }).click();
        await this.page.getByRole('button').filter({ hasText: /^$/ }).click();
        await this.page.waitForLoadState('networkidle');
    }

    async loadListSettings() {
        this.listSettings = await this.page.locator('[data-testid^="setting-item-"]').locator("div");
        this.listSettingCheckboxes = await this.page.locator('[data-testid^="setting-checkbox-"]');
    }

    async selectImportFileTemp(filePath: string) {
        
        const inputFile = await this.page.$('input[type="file"]');
        if (inputFile) {
            await inputFile.setInputFiles(filePath);
        }
    }
    async clickImportButton() {
        await this.importButton.click();
    }
    async clickNewItemButton() {
        await this.newItemButton.click();
    }
    async clickDeleteButton() {
        this.page.on('dialog', async dialog => {
            await dialog.accept(); 
        });
        await this.actionButton.click();
        await this.deleteButton.click();
    }
    async clickListSettingCheckbox(index: number) {
        await this.listSettingCheckboxes.nth(index).click();
    }
    async clickListSetting(index: number) {
        await this.listSettings.nth(index).click();
    }

}