import React, {useEffect, useState} from 'react';
import {useAuth} from "../../services/use-auth";
import {getStudentApplications, setApplicationsStatusWhenEnAttenteDeReponse} from "../../services/offerAppService";
import {Table, TableHeader, TableRow} from "../SharedComponents/Table/Table";
import MessageNothingToShow from "../SharedComponents/MessageNothingToShow/MessageNothingToShow";

export default function OfferApplicationsStudentSetStatusFinal() {
    const [offerApplications, setOfferApplications] = useState([])
    let auth = useAuth();
    useEffect(() => {
        if (!auth.user) {
            setOfferApplications([])
            return
        }
        getStudentApplications(auth.user.id).then(
            data => {
                setOfferApplications(data)
            })
    }, [auth.user])


    const updateStatus = (idOfferApp, isAccepted) => {
        setApplicationsStatusWhenEnAttenteDeReponse(idOfferApp, isAccepted).then((ok) => {
            if (ok)
                setOfferApplications((prevOffApp) => {
                    return prevOffApp.filter(offApp => offApp.id !== idOfferApp)
                })
        })
    };

    if (offerApplications.length === 0)
        return <MessageNothingToShow message="Vous n'avez pas d'offre de stage en attente de réponse"/>

    return (<>
        <Table>
            <TableHeader>
                <th>#</th>
                <th>Offre</th>
                <th>Status</th>
                <th>Changement de mon status</th>
            </TableHeader>
            {offerApplications.map(offerApplication => (
                <TableRow key={offerApplication.id}>
                    <th>{offerApplication.id}</th>
                    <td>{offerApplication.offer.title}</td>
                    <td>{offerApplication.status}</td>
                    <td>
                        <div className="btn-group">
                            <button className="btn btn-outline-success"
                                    onClick={() => updateStatus(offerApplication.id, true)}>Trouvé
                            </button>
                            <button className="btn btn-outline-danger"
                                    onClick={() => updateStatus(offerApplication.id, false)}>Refusé
                            </button>
                        </div>
                    </td>
                </TableRow>
            ))}
        </Table>
    </>)
}