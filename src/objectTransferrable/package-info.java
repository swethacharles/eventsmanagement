/**
 * 
 */
/**
 * Contains the transfer object to be transferred between the clients and the server
 * as well as the supertype Object transferrable which all of the objects are extended from
 * which allows the subtypes to be desseminated
 * 
 * 
 * {@link objectTransferrable.OTUsernameCheck} "0001"
 * {@link objectTransferrable.OTEmailCheck} = "0002"
 * {@link objectTransferrable.OTRegistrationInformation} = "0003"
 * 
 * **** OPEN OPCODE **** = "0004"
 * 
 * {@link objectTransferrable.OTExitGracefully} = "0005"
 * {@link objectTransferrable.OTRegistrationInformationConfirmation} = "0006"
 * {@link objectTransferrable.OTErrorResponse} = "0007"
 * {@link objectTransferrable.OTRequestMeetingsOnDay} = "0008"
 * {@link objectTransferrable.OTReturnDayEvents} = "0009"
 * {@link objectTransferrable.OTCreateEvent} = "0010"
 * {@link objectTransferrable.OTCreateEventSucessful} = "0011"
 * {@link objectTransferrable.OTLogin} = "0012"
 * {@link objectTransferrable.OTLoginSuccessful} = "0013"
 * {@link objectTransferrable.OTHeartBeat} = "0014"
 * {@link objectTransferrable.OTHashToClient} = "0015"
 * {@link objectTransferrable.OTLoginProceed} = "0016"
 * {@link objectTransferrable.OTUpdateEvent} = "0017"
 * {@link objectTransferrable.OTUpdateEventSuccessful} = "0018"
 * {@link objectTransferrable.OTDeleteEvent} = "0019"
 * {@link objectTransferrable.OTDeleteEventSuccessful} = "0020"
 * {@link objectTransferrable.OTUpdateUserProfile} = "0021"
 * {@link objectTransferrable.OTUpdateUserProfileSuccessful} = "0022"
 * {@link objectTransferrable.OTUpdatePassword} = "0023"
 * {@link objectTransferrable.OTUpdatePasswordSuccessful} = "0024"
 * {@link objectTransferrable.Event}
 * @author tmd668
 *
 */
package objectTransferrable;