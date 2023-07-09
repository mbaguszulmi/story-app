package co.mbznetwork.storyapp.datasource.api.model.response

data class GetStoryResponse(
    val story: Story = Story(),
    override val error: Boolean = false,
    override val message: String = ""
): BaseResponse()
