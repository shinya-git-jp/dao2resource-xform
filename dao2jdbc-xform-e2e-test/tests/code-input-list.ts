import { Locator, Page } from "@playwright/test";
import { InputTransformDialog } from "./code-input-transform-dialog";
import { InputDialogPage } from "./code-input-dialog";


export class InputListPage {
    readonly page: Page;
    readonly deleteButton: Locator;
    readonly newItemButton: Locator;
    readonly transformScriptButton: Locator;
    readonly globalSetting: Locator;
    readonly globalSettingExtendedLabel: Locator;
    readonly importButton: Locator;
    readonly globalSettingDetailsExtendedLabel: Locator;
    listInputs: Locator;
    listInputsCheckboxes: Locator;
    readonly inputDialog: InputDialogPage;
    readonly transformDialog: InputTransformDialog;
    readonly transformConditionSection: Locator;
    readonly globalSettingSection: Locator;
    readonly actionButton: Locator;

    constructor(page: Page) {
        this.page = page;
        this.deleteButton = page.getByRole("menuitem", { name: '削除' });
        this.newItemButton = page.getByRole('button', { name: '変換条件の新規作成' });
        this.transformScriptButton = page.getByRole('button', { name: '変換の実行' });
        this.globalSettingExtendedLabel = page.locator('div').filter({ hasText: /^グロバル設定$/ }).first();
        this.globalSettingDetailsExtendedLabel = page.locator('div').filter({ hasText: /^詳細$/ }).first();
        this.globalSetting = page.getByRole('combobox').first();
        this.inputDialog = new InputDialogPage(page);
        this.transformDialog = new InputTransformDialog(page);
        this.importButton = page.getByRole('button', { name: 'インポート' });
        this.transformConditionSection = page.getByRole("tab", { name: "変換条件の管理" });
        this.globalSettingSection = page.getByRole("tab", { name: "変換設定の選択" });
        this.actionButton = page.getByRole('button', { name: 'アクション' });
    }

    async openCodeInputPage() {
        await this.page.getByRole('button', { name: 'open drawer' }).click();
        await this.page.getByRole('button', { name: 'GEF-JDBCのAPIコード変換 GEF-DAO' }).click();
        await this.page.getByRole('button').filter({ hasText: /^$/ }).click();
        await this.page.waitForLoadState('networkidle');
    }

    async loadListInputs() {
        this.listInputs = await this.page.locator('[data-testid^="script-item-"]');
        this.listInputsCheckboxes = await this.page.locator('[data-testid^="script-checkbox-"]');
    }

    async clickDeleteButton() {
        await this.actionButton.click();
        this.page.on('dialog', async dialog => {
            await dialog.accept();
        });
        await this.deleteButton.click();
    }
    async selectTransformConditionSection() {
        this.transformConditionSection.click();
    }
    
    async selectGlobalSettingSection() {
        this.globalSettingSection.click();
    }
    async clickNewItemButton() {
        await this.newItemButton.click();
    }
    async clickTransformScriptButton() {
        await this.transformScriptButton.click();
    }
    async clickGlobalSettingExtendedLabel() {
        await this.globalSettingExtendedLabel.click();
    }
    async clickGlobalSettingDetailsExtendedLabel() {
        await this.globalSettingDetailsExtendedLabel.click();
    }
    async selectGlobalSetting(value: string) {
        await this.globalSetting.click();
        await this.page.getByRole('option', { name: value, exact: true }).click()
    }
    async clickListInputsCheckbox(index: number) {
        await this.listInputsCheckboxes.nth(index).click();
    }
    async clickListInputs(index: number) {
        await this.listInputs.nth(index).click();
    }
    async selectImportFileTemp(filePath: string) {
        await this.actionButton.click();
        const inputFile = await this.page.$('input[type="file"]');
        if (inputFile) {
            await inputFile.setInputFiles(filePath);
        }
    }
}