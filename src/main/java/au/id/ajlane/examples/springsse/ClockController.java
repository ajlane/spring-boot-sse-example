package au.id.ajlane.examples.springsse;

import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.Instant;
import java.util.Collection;
import java.util.concurrent.ConcurrentLinkedQueue;

@RestController
public class ClockController {

    private static final Collection<SseEmitter> emitters = new ConcurrentLinkedQueue<>();

    @RequestMapping("/clock")
    public SseEmitter subscribe() {
        SseEmitter emitter = new SseEmitter();
        emitter.onCompletion(() -> emitters.remove(emitter));
        emitters.add(emitter);
        return emitter;
    }

    @Scheduled(fixedRate = 100)
    public void tick() {
        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(Instant.now().toString(), MediaType.TEXT_PLAIN);
            } catch (IOException e) {
                emitter.complete();
            }
        }
    }
}
