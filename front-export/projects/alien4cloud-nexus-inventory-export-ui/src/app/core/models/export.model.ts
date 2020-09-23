export interface ExportResult {
    date: string;
    name: string;
    size: string;
    in_progress: boolean;
}

export class ExportRequest {
    name: string;
    items: ItemReference[];
}

export interface ItemReference {
    id: string;
    version: string;
}
