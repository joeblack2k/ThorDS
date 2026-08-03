	.syntax unified
	.arm

	/*
	 * ThorDS Smooth Orbit Camera v1 trampoline.
	 *
	 * The hook is installed at the generic camera update entry. It preserves
	 * the original prologue, accumulates smooth yaw in camera+0x184, then
	 * resumes at the first instruction after that prologue. Three separately
	 * verified target-bridge words make the active generic camera mode include
	 * that offset in its original ApproachAngle/position/collision path.
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
	ldr	r3, .L_free_camera_mode
	ldr	r1, [r8, #0x13c]
	cmp	r1, r3
	beq	.L_apply_delta
	mov	r0, r8
	ldr	r12, .L_enter_free_camera
	blx	r12
	ldr	r0, .L_protocol
.L_apply_delta:
	ldrsh	r1, [r0, #0x00]
	ldrh	r2, [r0, #0x04]
	mul	r1, r2, r1
	mov	r1, r1, asr #12
	add	r2, r8, #0x17c
	ldrsh	r3, [r2]
	add	r3, r3, r1
	ldr	r1, [r8, #0x110]
	ldrsh	r1, [r1, #0x8e]
	add	r1, r1, #0x8000
	sub	r3, r3, r1
	add	r2, r8, #0x184
	strh	r3, [r2]

.L_continue:
	ldr	pc, .L_original_continue

	.align	2
.L_protocol:
	.word	0x09000200
.L_magic:
	.word	0x00005343
.L_free_camera_mode:
	.word	0x020874cc
.L_enter_free_camera:
	.word	0x02005060
.L_original_continue:
	.word	0x02009e7c
	.size	thords_smooth_camera_hook, .-thords_smooth_camera_hook

	.global thords_pitch_bridge
	.type	thords_pitch_bridge, %function
thords_pitch_bridge:
	strh	r1, [r0, #0x7c]
	ldr	r0, .L_pitch_protocol
	ldrsh	r1, [r0, #0x02]
	ldr	r2, .L_pitch_scale
	mul	r1, r2, r1
	mov	r1, r1, asr #12
	add	r2, r8, #0x84
	ldr	r3, [r2]
	add	r3, r3, r1
	str	r3, [r2]
	ldr	pc, .L_pitch_continue

	.align	2
.L_pitch_protocol:
	.word	0x09000200
.L_pitch_scale:
	.word	0x00040000
.L_pitch_continue:
	.word	0x0200a7ac
	.size	thords_pitch_bridge, .-thords_pitch_bridge
