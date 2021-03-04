export interface ImportClaim {
  id: string;
  fileName: string;
  remotePath: string;
  user: string;
  body: string;
  status: ImportStatus;
  submitDate: string;
  endDate: string;
  progress: number;
}

export enum ImportStatus {
  Uploading = "Uploading",
  UploadError = "UploadError",
  Uploaded = "Uploaded",
  ValidationError = "ValidationError",
  Importing = "Importing",
  Imported = "Imported",
  ImportError = "ImportError"
}


