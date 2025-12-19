package dev.spiritstudios.spectre.impl.client;

import dev.spiritstudios.spectre.impl.Spectre;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.entity.state.EntityRenderState;

public class Flipray<T extends EntityRenderState> extends EntityModel<T> {
	// This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(Spectre.id("flipray"), "bb");
	private final ModelPart body;
	private final ModelPart rightWing;
	private final ModelPart rf1;
	private final ModelPart rf2;
	private final ModelPart rf3;
	private final ModelPart leftWing;
	private final ModelPart lf1;
	private final ModelPart lf2;
	private final ModelPart lf3;

	public Flipray(ModelPart root) {
		super(root);
		this.body = root.getChild("body");
		this.rightWing = this.body.getChild("rightWing");
		this.rf1 = this.rightWing.getChild("rf1");
		this.rf2 = this.rightWing.getChild("rf2");
		this.rf3 = this.rightWing.getChild("rf3");
		this.leftWing = this.body.getChild("leftWing");
		this.lf1 = this.leftWing.getChild("lf1");
		this.lf2 = this.leftWing.getChild("lf2");
		this.lf3 = this.leftWing.getChild("lf3");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(120, 98).addBox(-12.0F, -1.0F, -16.0F, 24.0F, 4.0F, 32.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition rightWing = body.addOrReplaceChild("rightWing", CubeListBuilder.create(), PartPose.offset(-9.0F, -1.0F, -5.0F));

		PartDefinition rightWing_r1 = rightWing.addOrReplaceChild("rightWing_r1", CubeListBuilder.create().texOffs(0, 0).addBox(-38.0F, -1.01F, -1.0F, 51.0F, 0.01F, 49.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-16.0F, 3.0F, 10.0F, -0.0436F, 0.0F, 0.0F));

		PartDefinition rightWing_r2 = rightWing.addOrReplaceChild("rightWing_r2", CubeListBuilder.create().texOffs(102, 98).addBox(-35.0F, -1.0F, 0.0F, 36.0F, 3.0F, 15.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, -8.0F, 0.0F, 0.2618F, 0.0F));

		PartDefinition rf1 = rightWing.addOrReplaceChild("rf1", CubeListBuilder.create(), PartPose.offsetAndRotation(-9.0F, -1.0F, -5.0F, 0.4363F, 0.0F, 0.0F));

		PartDefinition rf1_r1 = rf1.addOrReplaceChild("rf1_r1", CubeListBuilder.create().texOffs(120, 30).addBox(-13.0F, -2.0F, -1.0F, 14.0F, 2.0F, 35.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(7.0F, 0.0F, -4.0F, -0.2618F, 0.0F, 0.0F));

		PartDefinition rf2 = rightWing.addOrReplaceChild("rf2", CubeListBuilder.create(), PartPose.offset(-22.0F, -1.0F, -2.0F));

		PartDefinition rf2_r1 = rf2.addOrReplaceChild("rf2_r1", CubeListBuilder.create().texOffs(130, 113).addBox(-13.0F, -2.0F, -1.0F, 14.0F, 2.0F, 35.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(6.0F, 1.0F, -1.0F, 0.1745F, 0.0F, 0.0F));

		PartDefinition rf3 = rightWing.addOrReplaceChild("rf3", CubeListBuilder.create().texOffs(130, 65).addBox(-11.0F, -2.0F, -2.0F, 14.0F, 2.0F, 35.0F, new CubeDeformation(0.0F)), PartPose.offset(-32.0F, 0.0F, 0.0F));

		PartDefinition leftWing = body.addOrReplaceChild("leftWing", CubeListBuilder.create(), PartPose.offset(9.0F, -1.0F, -5.0F));

		PartDefinition leftWing_r1 = leftWing.addOrReplaceChild("leftWing_r1", CubeListBuilder.create().texOffs(102, 0).addBox(-3.0705F, -1.0F, -7.7274F, 36.0F, 3.0F, 15.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -0.2618F, 0.0F));

		PartDefinition leftWing_r2 = leftWing.addOrReplaceChild("leftWing_r2", CubeListBuilder.create().texOffs(0, 0).addBox(-13.0F, -1.01F, -1.0F, 51.0F, 0.01F, 49.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(16.0F, 3.0F, 10.0F, -0.0436F, 0.0F, 0.0F));

		PartDefinition lf1 = leftWing.addOrReplaceChild("lf1", CubeListBuilder.create(), PartPose.offsetAndRotation(9.0F, -1.0F, -5.0F, 0.4363F, 0.0F, 0.0F));

		PartDefinition lf1_r1 = lf1.addOrReplaceChild("lf1_r1", CubeListBuilder.create().texOffs(130, 113).addBox(-1.0F, -2.0F, -1.0F, 14.0F, 2.0F, 35.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-7.0F, 0.0F, -4.0F, -0.2618F, 0.0F, 0.0F));

		PartDefinition lf2 = leftWing.addOrReplaceChild("lf2", CubeListBuilder.create(), PartPose.offset(22.0F, -1.0F, -2.0F));

		PartDefinition lf2_r1 = lf2.addOrReplaceChild("lf2_r1", CubeListBuilder.create().texOffs(130, 30).addBox(-1.0F, -2.0F, -1.0F, 14.0F, 2.0F, 35.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-6.0F, 1.0F, -1.0F, 0.1745F, 0.0F, 0.0F));

		PartDefinition lf3 = leftWing.addOrReplaceChild("lf3", CubeListBuilder.create().texOffs(132, 130).addBox(-3.0F, -2.0F, -2.0F, 14.0F, 2.0F, 35.0F, new CubeDeformation(0.0F)), PartPose.offset(32.0F, 0.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 256, 256);
	}
}
