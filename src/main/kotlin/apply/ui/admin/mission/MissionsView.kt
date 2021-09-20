package apply.ui.admin.mission

import apply.application.MissionResponse
import apply.ui.admin.BaseLayout
import com.vaadin.flow.component.Component
import com.vaadin.flow.component.UI
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.html.H1
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.data.renderer.ComponentRenderer
import com.vaadin.flow.data.renderer.Renderer
import com.vaadin.flow.router.Route
import support.views.NEW_VALUE
import support.views.addSortableColumn
import support.views.addSortableDateTimeColumn
import support.views.createDeleteButtonWithDialog
import support.views.createPrimaryButton
import support.views.createPrimarySmallButton

@Route(value = "admin/missions", layout = BaseLayout::class)
class MissionsView : VerticalLayout() {
    init {
        add(createTitle(), createButton(), createGrid())
    }

    private fun createTitle(): Component {
        return HorizontalLayout(H1("과제 관리")).apply {
            setSizeFull()
            justifyContentMode = FlexComponent.JustifyContentMode.CENTER
        }
    }

    private fun createButton(): Component {
        return HorizontalLayout(
            createPrimaryButton("생성") {
                UI.getCurrent().navigate(MissionsFormView::class.java, NEW_VALUE)
            }
        ).apply {
            setSizeFull()
            justifyContentMode = FlexComponent.JustifyContentMode.END
        }
    }

    private fun createGrid(): Component {
        return Grid<MissionResponse>(10).apply {
            addSortableColumn("과제명", MissionResponse::title)
            addSortableColumn("모집명") { MissionResponse::recruitmentTitle }
            addSortableColumn("평가명") { MissionResponse::evaluationTitle }
            addSortableColumn("제출 가능 여부") { it.submittable.toText() }
            addSortableDateTimeColumn("시작일시", MissionResponse::startDateTime)
            addSortableDateTimeColumn("종료일시", MissionResponse::endDateTime)
            addColumn(createEditAndDeleteButton()).apply { isAutoWidth = true }
        }
    }

    private fun createEditAndDeleteButton(): Renderer<MissionResponse> {
        return ComponentRenderer<Component, MissionResponse> { it ->
            HorizontalLayout(
                createPrimarySmallButton("수정") {
                    // TODO 수정 뷰 이동
                },
                createDeleteButtonWithDialog("과제를 삭제하시겠습니까?") {
                    // TODO 삭제 기능 구현
                }
            )
        }
    }

    private fun Boolean.toText(): String {
        return if (this) {
            "가능"
        } else {
            "불가능"
        }
    }
}