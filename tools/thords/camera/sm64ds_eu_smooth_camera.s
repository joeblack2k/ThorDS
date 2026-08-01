	.syntax unified
	.arm

	/*
	 * ThorDS Smooth Orbit Camera v1 trampoline.
	 *
	 * The hook is installed at 0x02007CF0, immediately before the original
	 * 0x2000/0x0100 digital-yaw branches.  Invalid protocol state restores
	 * the original register set and returns to that instruction unchanged.
	 */
	.global thords_smooth_camera_hook
	.type thords_smooth_camera_hook, %function
thords_smooth_camera_hook:
	stmdb	sp!, {r0-r3, lr}
	ldr	r0, .L_protocol
	ldrh	r2, [r0, #0x08]
	ldr	r3, .L_magic
	cmp	r2, r3
	bne	.L_fallback
	ldrh	r2, [r0, #0x0a]
	cmp	r2, #1
	bne	.L_fallback
	ldrh	r2, [r0, #0x0c]
	tst	r2, #1
	beq	.L_fallback

	/* Consume one R3 sequence edge and reproduce the original recenter path. */
	ldrh	r3, [r0, #0x06]
	add	r1, r6, #0x100
	ldrh	r1, [r1, #0xa2]
	cmp	r3, r1
	beq	.L_yaw
	add	r1, r6, #0x100
	strh	r3, [r1, #0xa2]
	ldr	r1, [r6, #0x110]
	ldrsh	r1, [r1, #0x8e]
	add	r1, r1, #0x8000
	add	r2, r6, #0x100
	strh	r1, [r2, #0x9e]
	mov	r1, #1
	strh	r1, [r2, #0xa0]
	ldrh	r2, [r0, #0x0c]
	tst	r2, #2
	beq	.L_recenter
	mov	r0, #0x1a
	ldr	r3, .L_sound
	blx	r3
.L_recenter:
	ldmia	sp!, {r0-r3, lr}
	ldr	pc, .L_original_recenter

.L_yaw:
	ldrsh	r1, [r0, #0x00]
	ldrh	r2, [r0, #0x04]
	mul	r1, r2, r1
	mov	r1, r1, asr #12
	add	r2, r6, #0x184
	ldrsh	r3, [r2]
	add	r3, r3, r1
	strh	r3, [r2]
	ldmia	sp!, {r0-r3, lr}
	ldr	pc, .L_original_approach

.L_fallback:
	ldmia	sp!, {r0-r3, lr}
	ldr	pc, .L_original_digital

	.align	2
.L_protocol:
	.word	0x09000200
.L_magic:
	.word	0x00005343
.L_sound:
	.word	0x0200e790
.L_original_recenter:
	.word	0x02007b74
.L_original_approach:
	.word	0x02007d50
.L_original_digital:
	.word	0x02007cf0
	.size	thords_smooth_camera_hook, .-thords_smooth_camera_hook
