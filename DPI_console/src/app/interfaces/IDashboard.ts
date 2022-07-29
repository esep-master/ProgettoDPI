import { ISettore } from "./ISettore";

export interface IDashboard {
    numeroCommesseAttive: number,
    numeroKitNonAssociati: number,
    numeroDPINonAssociati: number,
    kitSettore: IKitSettore[],
    statiKit: IStatiKit
}

interface IKitSettore {
    settore: ISettore,
    numeroKit: number
}

interface IStatiKit {
    singleDPI: number,
    multiDPI: number,
    disattivati: number,
    ok: number
}