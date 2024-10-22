package tn.esprit.eventsproject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import tn.esprit.eventsproject.entities.Event;
import tn.esprit.eventsproject.entities.Logistics;
import tn.esprit.eventsproject.entities.Participant;
import tn.esprit.eventsproject.entities.Tache;
import tn.esprit.eventsproject.repositories.EventRepository;
import tn.esprit.eventsproject.repositories.LogisticsRepository;
import tn.esprit.eventsproject.repositories.ParticipantRepository;
import tn.esprit.eventsproject.services.EventServicesImpl;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class EventServicesTest {

    @InjectMocks
    private EventServicesImpl eventServices;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private ParticipantRepository participantRepository;

    @Mock
    private LogisticsRepository logisticsRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // 1. Test for addParticipant()
    @Test
    void testAddParticipant() {
        Participant participant = new Participant();
        when(participantRepository.save(participant)).thenReturn(participant);

        Participant result = eventServices.addParticipant(participant);

        verify(participantRepository, times(1)).save(participant);
        assertEquals(participant, result);
    }

    // 2. Test for addAffectEvenParticipant(Event event, int idParticipant)
    @Test
    void testAddAffectEvenParticipantWithId() {
        Event event = new Event();
        Participant participant = new Participant();
        participant.setIdPart(1);

        when(participantRepository.findById(1)).thenReturn(Optional.of(participant));
        when(eventRepository.save(event)).thenReturn(event);

        Event result = eventServices.addAffectEvenParticipant(event, 1);

        verify(participantRepository, times(1)).findById(1);
        verify(eventRepository, times(1)).save(event);
        assertEquals(event, result);
    }

    // 3. Test for addAffectEvenParticipant(Event event)
    @Test
    void testAddAffectEvenParticipantWithoutId() {
        Event event = new Event();
        Participant participant = new Participant();
        participant.setIdPart(1);
        Set<Participant> participants = new HashSet<>();
        participants.add(participant);
        event.setParticipants(participants);

        when(participantRepository.findById(1)).thenReturn(Optional.of(participant));
        when(eventRepository.save(event)).thenReturn(event);

        Event result = eventServices.addAffectEvenParticipant(event);

        verify(participantRepository, times(1)).findById(1);
        verify(eventRepository, times(1)).save(event);
        assertEquals(event, result);
    }

    // 4. Test for addAffectLog()
    @Test
    void testAddAffectLog() {
        Event event = new Event();
        event.setDescription("Event1");
        Logistics logistics = new Logistics();

        when(eventRepository.findByDescription("Event1")).thenReturn(event);
        when(logisticsRepository.save(logistics)).thenReturn(logistics);

        Logistics result = eventServices.addAffectLog(logistics, "Event1");

        verify(eventRepository, times(1)).findByDescription("Event1");
        verify(logisticsRepository, times(1)).save(logistics);
        assertEquals(logistics, result);
    }

    // 5. Test for getLogisticsDates()
    @Test
    void testGetLogisticsDates() {
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 12, 31);
        Event event = new Event();
        Logistics logistics = new Logistics();
        logistics.setReserve(true);
        Set<Logistics> logisticsSet = new HashSet<>();
        logisticsSet.add(logistics);
        event.setLogistics(logisticsSet);

        List<Event> events = Collections.singletonList(event); // Replaced List.of(event) with Collections.singletonList(event)

        when(eventRepository.findByDateDebutBetween(startDate, endDate)).thenReturn(events);

        List<Logistics> result = eventServices.getLogisticsDates(startDate, endDate);

        verify(eventRepository, times(1)).findByDateDebutBetween(startDate, endDate);
        assertEquals(1, result.size());
        assertTrue(result.contains(logistics));
    }

    // 6. Test for calculCout()
    @Test
    void testCalculCout() {
        Event event = new Event();
        event.setDescription("Event1");
        Logistics logistics = new Logistics();
        logistics.setReserve(true);
        logistics.setPrixUnit(10f);
        logistics.setQuantite(2);
        Set<Logistics> logisticsSet = new HashSet<>();
        logisticsSet.add(logistics);
        event.setLogistics(logisticsSet);

        List<Event> events = Collections.singletonList(event); // Replaced List.of(event) with Collections.singletonList(event)

        when(eventRepository.findByParticipants_NomAndParticipants_PrenomAndParticipants_Tache("Tounsi", "Ahmed", Tache.ORGANISATEUR)).thenReturn(events);
        when(eventRepository.save(event)).thenReturn(event);

        eventServices.calculCout();

        verify(eventRepository, times(1)).save(event);
        assertEquals(20f, event.getCout());
    }
}
