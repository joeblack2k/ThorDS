	.syntax unified
	.arm

	/*
	 * ThorDS Smooth Orbit Camera v1 trampoline.
	 *
	 * The hook is installed at the generic camera update entry. It preserves
	 * the original prologue, applies smooth yaw to the active camera object,
	 * then resumes at the first instruction after that prologue.
	 */
	.global thords_smooth_camera_hook
	.type thords_smooth_camera_hook, %function
thords_smooth_camera_hook:
	.word	0xE92D4FF0
	.word	0xE24DDF67
	.word	0xE1A08000
	ldr	r0, .L_protocol
	ldrh	r2, [r0, #0x08]
	ldr	r3, .L_magic
	cmp	r2, r3
	bne	.L_continue
	ldrh	r2, [r0, #0x0a]
	cmp	r2, #1
	bne	.L_continue
	ldrh	r2, [r0, #0x0c]
	tst	r2, #1
	beq	.L_continue
	ldrsh	r1, [r0, #0x00]
	ldrh	r2, [r0, #0x04]
	mul	r1, r2, r1
	mov	r1, r1, asr #12
	add	r2, r8, #0x184
	ldrsh	r3, [r2]
	add	r3, r3, r1
	strh	r3, [r2]

.L_continue:
	ldr	pc, .L_original_continue

	.align	2
.L_protocol:
	.word	0x09000200
.L_magic:
	.word	0x00005343
.L_original_continue:
	.word	0x02009e7c
	.size	thords_smooth_camera_hook, .-thords_smooth_camera_hook
