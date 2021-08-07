package apply.application

import apply.domain.recruitment.Recruitment
import apply.domain.recruitment.RecruitmentRepository
import apply.domain.recruitmentitem.RecruitmentItem
import apply.domain.recruitmentitem.RecruitmentItemRepository
import apply.domain.term.Term
import apply.domain.term.TermRepository
import apply.domain.term.getById
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import support.createLocalDateTime
import javax.annotation.PostConstruct

@Transactional
@Service
class RecruitmentService(
    private val recruitmentRepository: RecruitmentRepository,
    private val recruitmentItemRepository: RecruitmentItemRepository,
    private val termRepository: TermRepository
) {
    fun save(request: RecruitmentData) {
        val recruitment = recruitmentRepository.save(
            Recruitment(
                request.title,
                request.startDateTime,
                request.endDateTime,
                request.term.id,
                request.recruitable,
                request.hidden,
                request.id
            )
        )
        recruitmentItemRepository.deleteAll(
            findRecruitmentItemsToDelete(request.id, request.recruitmentItems.map { it.id })
        )
        recruitmentItemRepository.saveAll(
            request.recruitmentItems.map {
                RecruitmentItem(recruitment.id, it.title, it.position, it.maximumLength, it.description, it.id)
            }
        )
    }

    private fun findRecruitmentItemsToDelete(recruitmentId: Long, excludedItemIds: List<Long>): List<RecruitmentItem> {
        return recruitmentItemRepository
            .findByRecruitmentIdOrderByPosition(recruitmentId)
            .filterNot { excludedItemIds.contains(it.id) }
    }

    fun findAll(): List<RecruitmentResponse> {
        return recruitmentRepository.findAll()
            .map { RecruitmentResponse(it, termRepository.getById(it.termId)) }
    }

    fun findAllNotHidden(): List<RecruitmentResponse> {
        return recruitmentRepository.findAllByHiddenFalse()
            .map { RecruitmentResponse(it, termRepository.getById(it.termId)) }
    }

    fun deleteById(id: Long) {
        val recruitment = getById(id)
        check(!recruitment.recruitable)
        recruitmentRepository.delete(recruitment)
    }

    fun getById(id: Long): Recruitment =
        recruitmentRepository.findByIdOrNull(id) ?: throw IllegalArgumentException()

    fun getNotEndedDataById(id: Long): RecruitmentData {
        val recruitment = getById(id)
        val term = termRepository.getById(recruitment.termId)
        val recruitmentItems = recruitmentItemRepository.findByRecruitmentIdOrderByPosition(recruitment.id)
        return RecruitmentData(recruitment, term, recruitmentItems)
    }

    fun findAllTermSelectData(): List<TermSelectData> {
        val terms = listOf(Term.SINGLE) + termRepository.findAll().sortedBy { it.name }
        return terms.map(::TermSelectData)
    }

    @PostConstruct
    private fun populateDummy() {
        if (recruitmentRepository.count() != 0L) {
            return
        }
        val recruitments = listOf(
            Recruitment(
                title = "지원할 제목",
                startDateTime = createLocalDateTime(2020, 10, 5, 10),
                endDateTime = createLocalDateTime(2020, 11, 5, 10),
                recruitable = true,
                hidden = false
            ),
            Recruitment(
                title = "웹 백엔드 2기",
                startDateTime = createLocalDateTime(2019, 10, 25, 10),
                endDateTime = createLocalDateTime(2019, 11, 5, 10),
                recruitable = true,
                hidden = false
            ),
            Recruitment(
                title = "웹 백엔드 3기",
                startDateTime = createLocalDateTime(2020, 10, 25, 15),
                endDateTime = createLocalDateTime(2020, 11, 5, 10),
                recruitable = true,
                hidden = true
            ),
            Recruitment(
                title = "웹 프론트엔드 3기",
                startDateTime = createLocalDateTime(2020, 10, 25, 15),
                endDateTime = createLocalDateTime(2020, 11, 5, 10),
                recruitable = false,
                hidden = false
            )
        )
        recruitmentRepository.saveAll(recruitments)
    }
}
