package site.soloforest.soloforest.boundedContext.comment.entity;

// @Entity
// @Getter
// @Builder
// @EntityListeners(AuditingEntityListener.class)
// @AllArgsConstructor
// @NoArgsConstructor
// public class Comment {
// 	@Id
// 	@GeneratedValue(strategy = GenerationType.IDENTITY)
// 	private Long id;
//
// 	@ManyToOne
// 	private Account writer;
//
// 	@ManyToOne
// 	private Article article;
//
// 	@ToString.Exclude
// 	@Column(unique = true)
// 	@ManyToOne
// 	private Comment parent;
//
// 	@OneToMany(mappedBy = "parent", cascade = {CascadeType.REMOVE})
// 	@ToString.Exclude
// 	@OrderBy("id desc") // 정렬
// 	@Builder.Default // 빌더패턴 리스트시 초기화
// 	private List<Comment> children = new ArrayList<>();
//
// 	private String content;
//
// 	@CreatedDate
// 	private LocalDateTime createDate;
// 	@LastModifiedDate
// 	private LocalDateTime modifyDate;
//
// 	private Boolean secret;
//
// }
