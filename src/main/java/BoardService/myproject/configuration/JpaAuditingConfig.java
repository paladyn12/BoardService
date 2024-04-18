package BoardService.myproject.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing
// Auditing을 가능하게 하는 Config
// 엔티티 생성 및 변경 시 자동으로 값을 등록해줌
// 사용하려는 Base Entity에 @EntityListeners(AuditingEntityListener.class) 애노테이션
// 이후 Base Entity를 extends하여 해당 기능을 사용하려는 Entity 설정
public class JpaAuditingConfig {

}
