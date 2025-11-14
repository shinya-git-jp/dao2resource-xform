import * as yaml from 'js-yaml'; 
import { Buffer } from 'buffer';
import { z } from 'zod';
import { alpha, styled } from '@mui/material';
import { TreeItem, treeItemClasses } from '@mui/x-tree-view/TreeItem';
import { Parser } from 'node-sql-parser';
import JSEncrypt from 'jsencrypt';

export const readYaml = (file: any, callback: any) => {
    if (file) {
        const reader = new FileReader();
        reader.onload = function(e) {
            try {
                const yamlContent = e.target.result;
                const parsedData = yaml.load(yamlContent);
                console.log(parsedData);
                callback(parsedData);
            } catch (error) {
              console.log(error);
              alert('Error parsing YAML: フォーマットは正しくない！');
            }
        };
        
        reader.readAsText(file);
    }
};

export const encodeBase64SettingFile = (setting) => {
    const yamlStr = yaml.dump(setting);
    const base64String = Buffer.from(yamlStr).toString('base64');
    return base64String;
}


export const saveSettingsToBrowser = (settings: any) => {
    localStorage.setItem("settings", JSON.stringify(settings));
}
export const saveEntitiesToBrowser = (entities: any) => {
    localStorage.setItem("entities", JSON.stringify(entities));
}

export const exportFile = (object: any, fileName: any) => {
    const yamlData = yaml.dump(object);
    const blob = new Blob([yamlData], { type: 'application/x-yaml' });
    const link = document.createElement('a');
    link.href = URL.createObjectURL(blob);
    link.download = fileName + '.yaml';
    link.click();
}
export const SettingSchemaInput = z.object({
    id: z.string().optional(),
    savedName: z.string().optional(),
    db: z.object({
      url: z.string(),
      dbType: z.enum(["oracle", "mysql", "db2"]),
      schema: z.string(),
      username: z.string(),
      password: z.string()
    }),
    locale: z.object({
      current: z.string().optional(),
      mapping: z.object({
        country1: z.string(),
        country2: z.string().optional(),
        country3: z.string().optional(),
        country4: z.string().optional(),
        country5: z.string().optional()
      })
    }),
    reservedWords: z.object({
      primaryKey: z.string(),
      exclusiveKey: z.string(),
      companyCodeKey: z.string(),
      insertedUserIDKey: z.string(),
      insertedDateKey: z.string(),
      updatedUserIDKey: z.string(),
      updatedDateKey: z.string()
    }),
    transform: z.object({
      encoding: z.string().optional(),
      forceAliasColumn: z.boolean().optional(),
      useForeignKey: z.boolean().optional(),
      useExpMap: z.boolean().optional(),
      entityQuery: z.boolean().optional()
    }),
});

export const SettingSchema = z.object({
    id: z.string().optional(),
    savedName: z.string().optional(),
    db: z.object({
      url: z.string(),
      dbType: z.enum(["oracle", "mysql", "db2"]),
      schema: z.string(),
      username: z.string(),
      password: z.string()
    }),
    locale: z.object({
      current: z.string().optional(),
      mapping: z.object({
        country1: z.string(),
        country2: z.string().nullable().optional(),
        country3: z.string().nullable().optional(),
        country4: z.string().nullable().optional(),
        country5: z.string().nullable().optional()
      })
    }),
    reservedWords: z.object({
      primaryKey: z.string(),
      exclusiveKey: z.string(),
      companyCodeKey: z.string(),
      insertedUserIDKey: z.string(),
      insertedDateKey: z.string(),
      updatedUserIDKey: z.string(),
      updatedDateKey: z.string()
    }),
    transform: z.object({
      encoding: z.string().optional(),
      forceAliasColumn: z.boolean().optional(),
      useForeignKey: z.boolean().optional(),
      useExpMap: z.boolean().optional(),
      entityQuery: z.boolean().optional()
    }),
    savedTime: z.string()
});

export const SettingsSchema = z.array(SettingSchema);

export const EntitySchema = z.object({
    id: z.string(),
    savedName: z.string(),
    name: z.string(),
    phyName: z.string(),
    databaseName: z.string(),
    columns: z.array(z.object({
        id: z.number(),
        columnName: z.string(),
        phyName: z.string(),
        dataType: z.string(),
        size: z.string(),
        scale: z.string(),
        exclusiveKey: z.boolean(),
        generateType: z.string(),
        isPrimaryKey: z.boolean()
    })),
    uniqueKeys: z.array(z.object({})),
    foreignKeys: z.array(z.object({})),
    savedTime: z.string()
})

export const EntitiesSchema = z.array(EntitySchema)

export const EntitySchemaInput = z.object({
    name: z.string(),
    phyName: z.string(),
    databaseName: z.string(),
    columns: z.array(z.object({
        id: z.number(),
        columnName: z.string(),
        phyName: z.string(),
        dataType: z.string(),
        size: z.string(),
        scale: z.string(),
        exclusiveKey: z.boolean(),
        generateType: z.string(),
        isPrimaryKey: z.boolean()
    })),
    uniqueKeys: z.array(z.object({})),
    foreignKeys: z.array(z.object({})),
});


export const TitleTreeItem = styled(TreeItem)(({ theme }) => ({
  color: theme.palette.grey[200],
  [`& .${treeItemClasses.content}`]: {
    borderRadius: theme.spacing(0.5),
    padding: theme.spacing(0.5, 1),
    margin: theme.spacing(0.2, 0),
    [`& .${treeItemClasses.label}`]: {
      fontSize: '1rem',
      fontWeight: "bold",
    },
  },
  [`& .${treeItemClasses.iconContainer}`]: {
    borderRadius: '50%',
    backgroundColor: theme.palette.primary.dark,
    padding: theme.spacing(0, 1.2),
    ...theme.applyStyles('light', {
      backgroundColor: alpha(theme.palette.primary.main, 0.25),
    }),
    ...theme.applyStyles('dark', {
      color: theme.palette.primary.contrastText,
    }),
  },
  [`& .${treeItemClasses.groupTransition}`]: {
    marginLeft: 15,
    paddingLeft: 18,
    borderLeft: `1px dashed ${alpha(theme.palette.text.primary, 0.4)}`,
  },
  ...theme.applyStyles('light', {
    color: theme.palette.grey[800],
  }),
  
}));

export const CustomTreeItem = styled(TreeItem)(({ theme }) => ({
  color: theme.palette.grey[200],
  [`& .${treeItemClasses.content}`]: {
    borderRadius: theme.spacing(0.5),
    padding: theme.spacing(0.5, 1),
    margin: theme.spacing(0.2, 0),
    [`& .${treeItemClasses.label}`]: {
      fontSize: '0.8rem',
      fontWeight: 500,
    },
  },
  [`& .${treeItemClasses.iconContainer}`]: {
    borderRadius: '50%',
    backgroundColor: theme.palette.primary.dark,
    padding: theme.spacing(0, 1.2),
    ...theme.applyStyles('light', {
      backgroundColor: alpha(theme.palette.primary.main, 0.25),
    }),
    ...theme.applyStyles('dark', {
      color: theme.palette.primary.contrastText,
    }),
  },
  [`& .${treeItemClasses.groupTransition}`]: {
    marginLeft: 15,
    paddingLeft: 18,
    borderLeft: `1px dashed ${alpha(theme.palette.text.primary, 0.4)}`,
  },
  ...theme.applyStyles('light', {
    color: theme.palette.grey[800],
  }),
}));

export const EntityInputSchema = z.object({
  entityId: z.string().optional(),
  databaseId: z.string().optional()
})
export const EntityInputsSchema = z.array(EntityInputSchema);
export const parser = new Parser();

// export const encryptSettingPassword = (setting: Setting) => {
//   const tempSetting = {...setting, db: {...setting.db}};
//   tempSetting.db.password = encryptPassword(setting.db.password);
//   return tempSetting;
// }

// const encryptPassword = (password: string) => {
//   const publicKey = `
//   -----BEGIN PUBLIC KEY-----
// MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAmXYokxvsU7YSK5wqQ5zz
// sNytR6hqrFkLh1j0Jw4I4YKNOVVATLisYPNzCNwhyE23dLY/K1cNG06rarl/oZfE
// fNssw/q2axPdw1ZmNdsL56V7IwViHkondHor6syw64b1tdAUzKBAMuDKNMp8mN+R
// L1UZgXVzXajY6Pk1g/mPuW8DvBdGceZHW5d9ejOtiiGA13QnaedJ9PSBh0cMBb8R
// M6IkUziEh3GhIVbe3C9BMMxgUcusHhq01zEeil9Ge0/03C4F6JH7kCK0GKRsY7A4
// 5ZZNRQzsEjjDi+p5/YzBY9IaF17YtXDszaAtiLDws3q2gk8JBkVfXl2OIRk1/rnT
// +wIDAQAB
// -----END PUBLIC KEY-----
// `;
//  const payload = {
//     password: password,
//     timestamp: Date.now() ,
//     nonce: Math.random().toString(36).substring(2, 15)
//   };
//   const jsonString = JSON.stringify(payload);
//   const encryptor = new JSEncrypt();
//   encryptor.setPublicKey(publicKey);
//   const encrypted = encryptor.encrypt(jsonString);
//   if (!encrypted) {
//     throw new Error('Encryption failed');
//   }

//   return encrypted;
// }