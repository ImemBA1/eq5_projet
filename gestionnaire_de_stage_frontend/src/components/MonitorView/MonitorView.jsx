import React from "react";
import AddOffer from "./AddOffer/AddOffer";
import ViewAppliedStudents from "./ViewAppliedStudents/ViewAppliedStudents";
import {Route, useRouteMatch} from "react-router-dom";
import ContractsToBeSigned from "../Contract/ContractsToBeSigned";
import {UserType} from "../../enums/UserTypes";
import {ContainerBox} from "../SharedComponents/ContainerBox/ContainerBox";
import {useAuth} from "../../services/use-auth";

export default function MonitorView() {
    let {path} = useRouteMatch();
    let auth = useAuth();
    return <ContainerBox>
        <Route exact path={`${path}/offres/ajouter`}>
            <AddOffer/>
        </Route>
        <Route exact path={`${path}/applications`}>
            <ViewAppliedStudents/>
        </Route>
        <Route exact path={`${path}/voir/futures_stagiaires`}>
            <h1 className='text-center'>Contrats de futures stagiaires à valider</h1>
            <ContractsToBeSigned userType={UserType.MONITOR[0]}/>
        </Route>
        <Route exact path={`${path}`}>
            <h1 className="text-center">Bonjour {auth.user.firstName}!</h1>
        </Route>
    </ContainerBox>;
}