package work.service.util;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.stereotype.Service;
import work.domain.Event;
import work.domain.Member;
import work.repository.EventRepository;
import work.repository.MemberRepository;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

@Service
public class DataExportServiceBean implements DataExportService {
    private final EventRepository eventRepository;
    private final MemberRepository memberRepository;

    public DataExportServiceBean(EventRepository eventRepository, MemberRepository memberRepository) {
        this.eventRepository = eventRepository;
        this.memberRepository = memberRepository;
    }


    public void exportData() throws IOException {
        List<Event> events = eventRepository.findAll();
        List<Member> members = memberRepository.findAll();

        writeEventsToCSV(events, "events.csv");
        writeMembersToCSV(members, "members.csv");
    }

    private void writeEventsToCSV(List<Event> events, String filePath) throws IOException {
        try (CSVPrinter printer = new CSVPrinter(new FileWriter(filePath), CSVFormat.DEFAULT)) {
            for (Event event : events) {
                // Extract and format event data
                printer.printRecord(event.getId(), event.getName(), event.getCategories(), event.getLocation(), event.getStartDate());
            }
        }
    }

    private void writeMembersToCSV(List<Member> members, String filePath) throws IOException {
        try (CSVPrinter printer = new CSVPrinter(new FileWriter(filePath), CSVFormat.DEFAULT)) {
            for (Member member : members) {
                printer.printRecord(member.getId(), member.getUser().getId(), member.getEvent().getId(), member.getStatus(), member.getType(), member.getUser().getEmail());
            }
        }
    }
}
